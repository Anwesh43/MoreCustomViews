package com.example.rothorizbarvertview

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val colors : Array<Int> = arrayOf(
    "#3f51b5",
    "#f44336",
    "#00897b",
    "#f50057",
    "#00e676"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 3.9f
val barHFactor : Float = 9.6f
val delay : Long = 20
val rot : Float = 180f
val deg : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawRotHorizBarVert(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val barH : Float = Math.min(w, h) / barHFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(4)) {
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f, 1f - 2 * j)
                rotate(deg * dsc(3))
                drawRect(RectF(0f, 0f, size * dsc(j * 2), barH), paint)
            }
        }
        drawXY(0f, 0f) {
            rotate(rot * dsc(1) - deg * dsc(3))
            drawLine(0f, 0f, 0f, barH * Math.floor(dsc(0).toDouble()).toFloat(), paint)
        }
    }
}

fun Canvas.drawRHBVNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawRotHorizBarVert(scale, w, h, paint)
}

class RotHorizBarVertView(ctx : Context) : View(ctx) {

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

    data class RHBVNode(var i : Int = 0, val state : State = State()) {

        private var prev : RHBVNode? = null
        private var next : RHBVNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = RHBVNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRHBVNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RHBVNode {
            var curr : RHBVNode? = prev
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

    data class RotHorzBarVert(var i : Int) {

        private var curr : RHBVNode = RHBVNode(0)
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

    data class Renderer(var view : RotHorizBarVertView) {

        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rhbv : RotHorzBarVert = RotHorzBarVert(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            rhbv.draw(canvas, paint)
            animator.animate {
                rhbv.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            rhbv.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : RotHorizBarVertView {
            val view : RotHorizBarVertView = RotHorizBarVertView(activity)
            activity.setContentView(view)
            return view
        }
    }
}