package com.example.vaseflatlineview

import android.view.View
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.view.MotionEvent
import android.graphics.Canvas
import android.util.Log

val colors : Array<Int> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
).map {
    Color.parseColor(it)
}.toTypedArray()
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val parts : Int = 4
val scGap : Float = 0.04f / parts

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawWithoutDotLine(x1 : Float, y1 : Float, x2 : Float, y2 : Float, paint : Paint) {
    if (Math.abs(x1 - x2) < 0.1f && Math.abs(y1 - y2) < 0.1f) {
        return
    }
    drawLine(x1, y1, x2, y2, paint)
}

fun Canvas.drawVaseFlatLineView(scale : Float, w : Float, h : Float, paint : Paint) {
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val size : Float = Math.min(w, h) / sizeFactor
    save()
    translate(w / 2, h / 2 + (h / 2 + size) * dsc(3))
    rotate(-rot * dsc(2))
    drawWithoutDotLine(
        0f,
        -size * 0.5f * dsc(0),
        0f,
        size * 0.5f * dsc(0),
        paint
    )
    for (j in 0..1) {
        save()
        scale(1f, 1f - 2 * j)
        translate(0f, -size / 2)
        drawWithoutDotLine(
            0f,
            0f,
            (size / 2) * dsc(1),
            -(size / 4) * dsc(1),
            paint
        )
        restore()
    }
    restore()
}

fun Canvas.drawVFLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    //Log.d("draw VFLNode", "$scale")
    drawVaseFlatLineView(scale, w , h, paint)
}

class VaseFlatLineView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        //Log.d("ONDraw", "VaseFlatLineView")
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
            //Log.d("Updating", "State class")
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

            //Log.d("Animating", "Animator")
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

    data class VFLNode(var i : Int, val state : State = State()) {

        private var prev : VFLNode? = null
        private var next : VFLNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = VFLNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            //Log.d("Drawing", "VaseFlatLine")
            canvas.drawVFLNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : VFLNode {
            var curr : VFLNode? = prev
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

    data class VaseFlatLine(var i : Int) {

        private var curr : VFLNode = VFLNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            //Log.d("Drawing", "VaseFlatLine")
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

        fun startUpdating(cb  : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : VaseFlatLineView) {

        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val vfl : VaseFlatLine = VaseFlatLine(0)

        fun render(canvas : Canvas) {
            //Log.d("Drawing", "Renderer")
            canvas.drawColor(backColor)
            vfl.draw(canvas, paint)
            animator.animate {
                vfl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            vfl.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity: Activity) : VaseFlatLineView {
            val view : VaseFlatLineView = VaseFlatLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}