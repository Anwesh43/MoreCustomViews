package com.example.bilinebardownview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color

val colors : Array<Int> = arrayOf(
    "#AB47BC",
    "#1E88E5",
    "#00E678",
    "#FF3D00",
    "#3D5AFE"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val barWFactor : Float = 24.2f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val deg : Float = 180f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawLine(x1 : Float, y1 : Float, x2 : Float, y2 : Float, paint : Paint) {
    if (Math.abs(x1 - x2) < 0.1f && Math.abs(y1 - y2) < 0.1f) {
        return
    }
    drawLine(x1, y1, x2, y2, paint)
}

fun Canvas.drawBiLineBarDown(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val barSize : Float = Math.min(w, h) / barWFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(4)) {
        rotate(deg * dsc(2))
        drawLine(-size * 0.5f * dsc(0), 0f, size * 0.5f * dsc(0), 0f, paint)
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f - 2 * j, 1f)
                drawXY(size / 2 - barSize, 0f ) {
                    drawRect(RectF(0f, 0f, barSize, size * dsc(1)), paint)
                }
            }
        }
        drawLine(0f, 0f, 0f, -size * dsc(3), paint)
    }
}

fun Canvas.drawBLBDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBiLineBarDown(scale, w, h, paint)
}

class BiLineBarDownView(ctx : Context) : View(ctx) {

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

    data class BLBDNode(var i : Int, val state : State = State()) {

        private var prev : BLBDNode? = null
        private var next : BLBDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = BLBDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBLBDNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BLBDNode {
            var curr : BLBDNode? = prev
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

    data class BiLineBarDown(var i : Int) {

        private var curr : BLBDNode = BLBDNode(0)
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

    data class Renderer(var view : BiLineBarDownView) {

        private val animator : Animator = Animator(view)
        private val biLineBarDown : BiLineBarDown = BiLineBarDown(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            biLineBarDown.draw(canvas, paint)
            animator.animate {
                biLineBarDown.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            biLineBarDown.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BiLineBarDownView {
            val view : BiLineBarDownView = BiLineBarDownView(activity)
            activity.setContentView(view)
            return view
        }
    }
}