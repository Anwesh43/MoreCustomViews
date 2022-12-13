package com.example.lineopenbarmiddleview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

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
val rot : Float = 90f
val delay : Long = 20
val barWFactor : Float = 16.2f
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

fun Canvas.drawLineOpenBarMiddle(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val barW : Float = Math.min(w, h) / barWFactor
    drawXY(w / 2, h / 2 - (h / 2) * dsc(4)) {
        for (j in 0..1) {
            drawXY(0f, 0f) {
                rotate(90f * (1 - 2 * j) * dsc(1))
                drawLine(0f, 0f, 0f, -size * dsc(0), paint)
            }
            drawXY(-size + (size - barW / 2) * dsc(3), 0f) {
                drawRect(RectF(0f, -size * dsc(2), barW, 0f), paint)
            }
        }
    }
}

fun Canvas.drawLOBMNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineOpenBarMiddle(scale, w, h, paint)
}
