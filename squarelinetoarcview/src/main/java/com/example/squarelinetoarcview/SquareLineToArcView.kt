package com.example.squarelinetoarcview

import android.view.View
import android.view.MotionEvent
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Paint
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
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawSquareToLineArc(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2 + size) * dsc(4) , h / 2) {
        rotate(rot * dsc(3))
        drawXY(0f, (h / 2 + size / 2) * (1 - dsc(0))) {
            drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
        }
        drawXY(0f, -size / 2 - (h / 2 - size / 2) * (1 - dsc(1))) {
            for (j in 0..1) {
                drawXY(0f, 0f) {
                    rotate(rot * dsc(2) * (1 - 2 * j))
                    drawLine(0f, 0f, 0f, -size / 2, paint)
                }
            }
            drawArc(
                RectF(-size / 2, -size / 2, size / 2, size / 2),
                180f + rot - rot * dsc(2),
                rot * 2 * dsc(2),
                true,
                paint
            )
        }
    }
}

fun Canvas.drawSLTANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawSquareToLineArc(scale, w, h, paint)
}

class SquareLineToArcView(ctx : Context) : View(ctx) {

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

    data class SLTANode(var i : Int = 0, val state : State = State()) {

        private var next : SLTANode? = null
        private var prev : SLTANode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SLTANode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSLTANode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SLTANode {
            var curr : SLTANode? = prev
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

    data class SquareLineToArc(var i : Int = 0) {

        private var curr : SLTANode = SLTANode(0)
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

    data class Renderer(var view : SquareLineToArcView) {

        private val slta : SquareLineToArc = SquareLineToArc(0)
        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            slta.draw(canvas, paint)
            animator.animate {
                slta.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            slta.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : SquareLineToArcView {
            val view : SquareLineToArcView = SquareLineToArcView(activity)
            activity.setContentView(view)
            return view
        }
    }
}