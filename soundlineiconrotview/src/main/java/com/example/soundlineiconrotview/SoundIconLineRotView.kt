package com.example.soundlineiconrotview

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
val parts : Int = 6
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val rot : Float = 90f
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

fun Canvas.drawLineWithoutDot(x1 : Float, y1 : Float, x2 : Float, y2 : Float, paint : Paint) {
    if (Math.abs(x1 - x2) < 0.1f && Math.abs(y1 - y2) < 0.1f) {
        return
    }
    drawLine(x1, y1, x2, y2, paint)
}

fun Canvas.drawSoundLineIconRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    drawXY(w / 2, h / 2 - (h / 2 + size) * dsc(5)) {
        rotate(rot * dsc(4))
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f, 1f - 2 * j)
                drawLineWithoutDot(0f, 0f, 0f, -size * 0.5f * dsc(0), paint)
                drawXY(0f, -size * 0.5f) {
                    drawLineWithoutDot(0f, 0f, size * 0.5f * dsc(1), 0f, paint)
                }
                drawXY(size * 0.5f, -size * 0.5f) {
                    drawLineWithoutDot(0f, 0f, size * 0.5f * dsc(2), -size * 0.5f * dsc(2), paint)
                }
                drawXY(size, -size) {
                    drawLineWithoutDot(0f, 0f, 0f, size * dsc(3), paint)
                }
            }
        }
    }
}

fun Canvas.drawSILRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawSoundLineIconRot(scale, w, h, paint)
}

class SoundIconLineRotView(ctx : Context) : View(ctx) {

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

    data class SILRNode(var i : Int = 0, val state : State = State()) {

        private var prev : SILRNode? = null
        private var next : SILRNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SILRNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSILRNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SILRNode {
            var curr : SILRNode? = prev
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

    data class SoundIconLineRot(var i : Int) {

        private var curr : SILRNode = SILRNode(0)
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

    data class Renderer(var view : SoundIconLineRotView) {

        private val animator : Animator = Animator(view)
        private val silr : SoundIconLineRot = SoundIconLineRot(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            silr.draw(canvas, paint)
            animator.animate {
                silr.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            silr.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : SoundIconLineRotView {
            val view : SoundIconLineRotView = SoundIconLineRotView(activity)
            activity.setContentView(view)
            return view
        }
    }
}