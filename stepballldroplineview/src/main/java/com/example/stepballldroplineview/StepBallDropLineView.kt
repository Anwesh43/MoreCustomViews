package com.example.stepballldroplineview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
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
val steps : Int = 3
val parts : Int = 2 * steps - 1
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 13.9f
val delay : Long = 20
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

fun Canvas.drawStepBallDropLine(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val wGap : Float = (w - size) / (steps - 1)
    drawXY(0f, h / 2) {
        for (j in 0..(steps - 1)) {
            drawXY(wGap * j, 0f) {
                drawCircle(size / 2 + wGap * j, (-h / 2 - size) * (1 - dsc(j * 2)), size / 2, paint)
            }
        }
        for (j in 0..(steps - 1)) {
            drawXY((w / 2) * j, 0f) {
                drawLine(0f, 0f, w * 0.5f * dsc(2 * j + 1), 0f, paint)
            }
        }
    }
}

fun Canvas.drawSBDLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawStepBallDropLine(scale, w, h, paint)
}
