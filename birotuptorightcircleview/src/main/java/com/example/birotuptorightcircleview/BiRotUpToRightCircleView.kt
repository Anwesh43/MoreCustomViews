package com.example.birotuptorightcircleview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.content.Context
import android.app.Activity

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
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val rot : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
val rFactor : Float = 21.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawBiRotUpToRightCircle(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val r : Float = Math.min(w, h) / rFactor
    drawXY(w / 2, h / 2) {
        rotate(rot * dsc(2))
        for (j in 0..1) {
            drawXY(-size * (1f - 2 * j), -h / 2 - r + (h / 2 + r) * dsc(j) + (w / 2 + r) * dsc(3 + j)) {
                drawCircle(0f, 0f, r, paint)
            }
        }
    }
}

fun Canvas.drawBRUTRCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBiRotUpToRightCircle(scale, w, h, paint)
}

class BiRotUpToRightCircleView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
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

    data class BUTRCNode(var i : Int = 0, val state : State = State()) {

        private var next : BUTRCNode? = null
        private var prev : BUTRCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = BUTRCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBRUTRCNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BUTRCNode {
            var curr : BUTRCNode? = prev
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

    data class BiRotUpToRight(var i : Int) {

        private var curr : BUTRCNode = BUTRCNode(0)
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

    data class Renderer(var view : BiRotUpToRightCircleView) {

        private val animator : Animator = Animator(view)
        private val butrc : BiRotUpToRight = BiRotUpToRight(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            butrc.draw(canvas, paint)
            animator.animate {
                butrc.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            butrc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : BiRotUpToRightCircleView {
            val view : BiRotUpToRightCircleView = BiRotUpToRightCircleView(activity)
            activity.setContentView(view)
            return view
        }
    }
}