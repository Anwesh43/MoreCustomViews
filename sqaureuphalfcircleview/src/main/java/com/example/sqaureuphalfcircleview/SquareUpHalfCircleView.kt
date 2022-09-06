package com.example.sqaureuphalfcircleview

import android.view.View
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.view.MotionEvent
import android.graphics.Canvas

val colors : Array<Int> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val rFactor : Float = 18.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawSquareUpHalfCircle(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val r : Float = Math.min(w, h) / rFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    save()
    translate(w / 2, h / 2 + (h / 2 + size) * dsc(3))
    rotate(rot * dsc(2))
    drawRect(RectF(-size * 0.5f * dsc(0), -size / 5, size * 0.5f * dsc(0), size / 5), paint)
    for (j in 0..1) {
        save()
        scale(1f - 2 * j, 1f - 2 * j)
        translate(-size / 2 + r, -size / 5)
        drawArc(RectF(-r, -r, r, r), 180f , 180f * dsc(1), true, paint)
        restore()
    }
    restore()
}

fun Canvas.drawSUHCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    drawSquareUpHalfCircle(scale, w, h, paint)
}

class SquareUpHalfCircleView(ctx : Context) : View(ctx) {

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
                view.postInvalidate()
            }
        }
    }

    data class SUHCNode(var i : Int, val state : State = State()) {

        private var prev : SUHCNode? = null
        private var next : SUHCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SUHCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSUHCNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SUHCNode {
            var curr : SUHCNode? = prev
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

    data class SquareUpHalfCircle(var i : Int) {

        private var curr : SUHCNode = SUHCNode(0)
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

    data class Renderer(var view : SquareUpHalfCircleView) {

        private val animator : Animator = Animator(view)
        private val squareUpHalfCircle : SquareUpHalfCircle = SquareUpHalfCircle(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            squareUpHalfCircle.draw(canvas, paint)
            animator.animate {
                squareUpHalfCircle.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            squareUpHalfCircle.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : SquareUpHalfCircleView {
            val view : SquareUpHalfCircleView = SquareUpHalfCircleView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
