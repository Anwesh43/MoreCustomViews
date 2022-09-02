package com.example.linemultirightlineview

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent

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

fun Canvas.drawMultiRightLine(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    drawXY(w / 2 - (w / 2 + size) * dsc(3) , h / 2) {
        drawLine(0f, 0f, 0f, -size * dsc(0), paint)
        drawXY(0f, 0f) {
            rotate(-rot * dsc(1))
            drawLine(0f, 0f, 0f, -size * Math.floor(dsc(0).toDouble()).toFloat(), paint)
        }
        drawXY(-size, 0f) {
            rotate(-rot * dsc(2))
            drawLine(0f, 0f, size * Math.floor(dsc(1).toDouble()).toFloat(), 0f, paint)
        }
    }
}

fun Canvas.drawMRLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawMultiRightLine(scale, w, h, paint)
}

