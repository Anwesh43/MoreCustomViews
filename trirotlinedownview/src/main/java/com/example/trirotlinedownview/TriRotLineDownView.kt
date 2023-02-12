package com.example.trirotlinedownview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Path

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
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val strokeFactor : Float = 90f
val triSizeFactor : Float = 15.9f
val sizeFactor : Float = 4.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawTriangle(x : Float, y : Float, size : Float, paint : Paint) {
    drawXY(x, y) {
        val path : Path = Path()
        path.moveTo(0f, -size / 2)
        path.lineTo(size, 0f)
        path.lineTo(0f, size / 2)
        path.lineTo(0f, -size / 2)
        drawPath(path, paint)
    }
}

fun Canvas.drawTriRotLine(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val triSize : Float = Math.min(w, h) / triSizeFactor
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(4)) {
        drawXY(-w / 2 + (w / 2) * dsc(0), 0f) {
            rotate(rot * dsc(2))
            drawLine(0f, 0f, -size, 0f, paint)
        }
        drawXY(w / 2 - (w / 2) * dsc(1), 0f) {
            rotate(rot * dsc(3))
            drawTriangle(0f, 0f, triSize, paint)
        }
    }
}

fun Canvas.drawTRLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawTriRotLine(scale, w, h, paint)
}

class TriRotLineDownView(ctx : Context) : View(ctx) {

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