package com.example.multiarrowsmoverview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

val colors : Array<Int> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 3
val scGap : Float = 0.02f / parts
val lines : Int = 4
val sizeFactor : Float = 18.2f
val delay : Long = 20
val deg : Float = 30f
val backColor : Int = Color.parseColor("#BDBDBD")
val strokeFactor : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawMultiArrowsMover(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sc1 : Float = scale.divideScale(0, parts)
    val sc2 : Float = scale.divideScale(1, parts)
    val sc3 : Float = scale.divideScale(2, parts)
    val lSize : Float = size / lines
    save()
    translate(w / 2, h / 2)
    for (j in 0..(lines - 1)) {
        val sc1j : Float = sc1.divideScale(j, parts)
        val sc2j : Float = sc2.divideScale(j, parts)
        val sc3j : Float = sc3.divideScale(j, parts)
        save()
        translate(-w / 2 + (w / 2) * sc1j + (w / 2 + lSize) * sc3j, -h / 2 - lSize / 2 + lSize * j)
        for (k in 0..1) {
            save()
            rotate(deg * (1 - 2 * k) * sc2j)
            drawLine(0f, 0f, -lSize, 0f, paint)
            restore()
        }
        restore()
    }
    restore()
}

fun Canvas.drawMAMNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.color = colors[i]
    drawMultiArrowsMover(scale, w, h, paint)
}

class MultiArrowsMoverView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class MAMNode(var i : Int, val state : State = State()) {

        private var next : MAMNode? = null
        private var prev : MAMNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = MAMNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawMAMNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : MAMNode {
            var curr : MAMNode? = prev
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

    data class MultiArrowsMover(var i : Int) {

        private var curr : MAMNode = MAMNode(0)
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

    data class Renderer(var view : MultiArrowsMoverView) {

        private val mam : MultiArrowsMover = MultiArrowsMover(0)
        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            mam.draw(canvas, paint)
            animator.animate {
                mam.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            mam.startUpdating {
                animator.start()
            }
        }
    }
}