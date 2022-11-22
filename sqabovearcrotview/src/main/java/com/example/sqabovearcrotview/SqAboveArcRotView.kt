package com.example.sqabovearcrotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Canvas

val colors : Array<Int> = arrayOf(
    "#AB47BC",
    "#1E88E5",
    "#00E678",
    "#FF3D00",
    "#3D5AFE"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 6
val scGap : Float = 0.05f / parts
val rot : Float = 90f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val sizeFactor : Float = 4.9f
val sqFactor : Float = 3.2f
val strokeFactor : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawSqAboveRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val sqSize : Float = size / sqFactor
    drawXY(w / 2 + (w / 2 + size) * dsc(5), h / 2) {
        rotate(-rot * dsc(3))
        drawArc(RectF(-size, -size, size, size), 0f, 180f * dsc(0), true, paint)
        drawXY(-size + (size - sqSize / 2) * dsc(2), 0f) {
            drawRect(RectF(0f, -sqSize * dsc(1), sqSize, 0f), paint)
        }
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f - 2 * j, 1f)
                drawXY(size, 0f) {
                    drawLine(0f, 0f, 0f, -(size / 2) * dsc(4), paint)
                }
            }
        }
    }
}

fun Canvas.drawSAARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    drawSqAboveRot(scale,w , h, paint)
}

class SqAboveArcRotView(ctx : Context) : View(ctx) {

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

    data class SAARNode(var i : Int = 0, val state : State = State()) {

        private var next : SAARNode? = null
        private var prev : SAARNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SAARNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSAARNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SAARNode {
            var curr : SAARNode? = prev
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

    data class SqAboveArcRot(var i : Int) {

        private var curr : SAARNode = SAARNode(0)
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

    data class Renderer(var view : SqAboveArcRotView) {

        private val animator : Animator = Animator(view)
        private val saar : SqAboveArcRot = SqAboveArcRot(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            saar.draw(canvas, paint)
            animator.animate {
                saar.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            saar.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : SqAboveArcRotView {
            val view : SqAboveArcRotView = SqAboveArcRotView(activity)
            activity.setContentView(view)
            return view
        }
    }
}