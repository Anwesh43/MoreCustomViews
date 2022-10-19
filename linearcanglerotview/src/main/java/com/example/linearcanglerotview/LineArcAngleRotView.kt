package com.example.linearcanglerotview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Canvas
import android.content.Context
import android.app.Activity

val colors : Array<Int> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
).map {
    Color.parseColor(it)
}.toTypedArray()
val deg : Float = 90f
val rot : Float = 60f
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
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

fun Canvas.drawLineArcAngleRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / 4.9f
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w /2, h / 2) {
        rotate(deg * dsc(2))
        for (j in 0..1) {
            drawXY(0f, 0f) {
                rotate(-rot * dsc(1))
                drawLine(0f, 0f, 0f, -size * dsc(0), paint)
            }
        }
        drawArc(RectF(-size / 4, -size / 4, size / 4, size / 4), 270 - rot * dsc(1), rot * dsc(1), true, paint)
    }
}

fun Canvas.drawLAARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineArcAngleRot(scale, w, h, paint)
}
