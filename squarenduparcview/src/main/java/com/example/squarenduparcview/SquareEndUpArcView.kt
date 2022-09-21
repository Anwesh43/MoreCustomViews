package com.example.squarenduparcview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color
import android.graphics.Canvas

val colors : Array<Int> = arrayOf(
    "#039BE5",
    "#BF360C",
    "#006064",
    "#E91E63",
    "#0D47A1"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val rFactor : Float = 7.8f
val delay : Long = 20
val rot : Float = 360f
val deg : Float = 45f
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

fun Canvas.drawSquareEndUpArc(scale : Float, w : Float, h : Float, paint : Paint) {
    val r : Float = Math.min(w, h) / r
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    drawXY(w / 2, h / 2 + (h / 2 + r) * dsc(3)) {
        rotate(deg * dsc(2))
        drawArc(RectF(-r, -r, r, r), 0f, rot * dsc(0), true, paint)
        for (j in 0..1) {
            drawXY(-r, -r) {
                rotate(deg * 2 * j)
                drawLine(r * (1 - dsc(1)), 0f, r, 0f, paint)
            }
        }
    }
}

fun Canvas.drawSEUANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) /strokeFactor
    drawSquareEndUpArc(scale, w, h, paint)
}
