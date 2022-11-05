package com.example.doublesquarealtrotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val colors : Array<Int> = arrayOf(
    "#AB47BC",
    "#1E88E5",
    "#00E678",
    "#FF3D00",
    "#3D5AFE"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 4
val scGap : Float = 0.04f / parts
val sizeFactor : Float = 3.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBBD")
val rot : Float = 180f
val barSizeFactor : Float = 6.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawDoubleSquareAltRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val barSize : Float = size / barSizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val upBarSize : Float = barSize * dsc(0)
    val a : Float = (size / 2 - barSize / 2) * (1 - dsc(2))
    drawXY(w / 2, h / 2 + (h / 2 + barSize) * dsc(3)) {
        rotate(rot * dsc(1))
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f - 2 * j, 1f - 2 * j)
                drawXY(a, a) {
                    drawRect(RectF(-upBarSize / 2, -upBarSize / 2, upBarSize / 2, upBarSize / 2), paint)
                }
            }
        }

    }
}

fun Canvas.drawDSARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawDoubleSquareAltRot(scale, w, h, paint)
}

class DoubleSquareAltRotView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

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
}