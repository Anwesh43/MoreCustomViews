package com.example.sqaureuphalfcircleview

import android.view.View
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.view.MotionEvent
import android.graphics.Canvas

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
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val rFactor : Float = 14.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawSquareUpHalfCircle(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val r : Float = Math.min(w, h) / rFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    save()
    translate(w / 2, h / 2 + (h / 2 + size) * dsc(3))
    rotate(rot * dsc(2))
    drawRect(RectF(-size * 0.5f * dsc(0), -size / 5, size * 0.5f * dsc(0), size / 5), paint)
    for (j in 0..1) {
        save()
        scale(1f - 2 * j, 1f - 2 * j)
        translate(-size / 2, -size / 5)
        drawArc(RectF(-r, -r, r, r), 180f , 180f * dsc(1), true, paint)
        restore()
    }
    restore()
}

fun Canvas.drawSUHCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    drawSquareUpHalfCircle(scale, w, h, paint)
}
