package com.example.barlinebiarcrotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
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
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val start : Float = rot * 3
fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawLineWithoutDot(x1 : Float, y1 : Float, x2 : Float, y2 : Float, paint : Paint) {
    if (Math.abs(x1 - x2) < 0.1f && Math.abs(y1 - y2) < 0.1f) {
        return
    }
    drawLine(x1, y1, x2, y2, paint)
}

fun Canvas.drawBarLineBiArcRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2 + size / 2) * dsc(4), h / 2) {
        rotate(rot * dsc(3))
        drawXY((w / 2 + size / 2) * (1 - dsc(0)), 0f) {
            paint.style = Paint.Style.FILL
            drawRect(RectF(-size / 2, 0f, size / 2, size / 2), paint)
        }
        drawLineWithoutDot(0f, 0f, 0f, -size * 0.5f * dsc(1), paint)
        paint.style = Paint.Style.STROKE
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f - 2 * j, 1f)
                drawArc(
                    RectF(
                        -size / 2,
                        -size / 2,
                        size / 2,
                        size / 2
                    ), start, rot * dsc(2), false, paint)
            }
        }
    }
}

fun Canvas.drawBLBARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor

    drawBarLineBiArcRot(scale, w, h, paint)
}

class BarLineBiArcRotView(ctx : Context) : View(ctx) {

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

    data class BLBARNode(var i : Int = 0, val state : State = State()) {

        private var next : BLBARNode? = null
        private var prev : BLBARNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = BLBARNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBLBARNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BLBARNode {
            var curr : BLBARNode? = prev
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

    data class BarLineBiArcRot(var i : Int) {

        private var curr : BLBARNode = BLBARNode(0)
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

    data class Renderer(var view : BarLineBiArcRotView) {

        private val animator : Animator = Animator(view)
        private val blbar : BarLineBiArcRot = BarLineBiArcRot(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            blbar.draw(canvas, paint)
            animator.animate {
                blbar.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            blbar.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BarLineBiArcRotView {
            val view : BarLineBiArcRotView = BarLineBiArcRotView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
