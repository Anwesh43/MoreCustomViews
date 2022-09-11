package com.example.blocktravellerview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
import android.content.Context
import android.app.Activity

val colors : Array<Int> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
).map {
    Color.parseColor(it)
}.toTypedArray()
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val barHFactor : Float = 4.9f
val barWFactor : Float = 10.2f
val parts : Int = 5
val scGap : Float = 0.04f / parts
val rot : Float = 45f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawBlockTraveller(scale : Float, w : Float, h : Float, paint : Paint) {
    val barH : Float = Math.min(w, h) / barHFactor
    val barW : Float = Math.min(w, h) / barWFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    save()
    translate(w / 2 + (w / 4) * dsc(2), h / 2 + (h / 4) * dsc(2) + (h/ 4 + barH / 2) * dsc(4))
    rotate(rot * (dsc(1) - dsc(3)))
    drawRect(RectF(-barW / 2, barH / 2 - barH * dsc(0), barW / 2, barH / 2), paint)
    restore()
}

fun Canvas.drawBTNode(i : Int, scale : Float, paint : Paint) {
    paint.color = colors[i]
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    drawBlockTraveller(scale, w, h, paint)
}

class BlockTravellerView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BTNode(var i : Int, val state : State = State()) {

        private var prev : BTNode? = null
        private var next : BTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = BTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBTNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BTNode {
            var curr : BTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BlockTraveller(var i : Int, val state : State = State()) {

        private var curr : BTNode = BTNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BlockTravellerView) {

        private val bt : BlockTraveller = BlockTraveller(0)
        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            bt.draw(canvas, paint)
            animator.animate {
                bt.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bt.startUpdating {
                animator.start()
            }
        }
    }
}