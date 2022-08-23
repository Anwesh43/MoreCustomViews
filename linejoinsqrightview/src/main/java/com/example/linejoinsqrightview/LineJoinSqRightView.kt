package com.example.linejoinsqrightview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color
import android.graphics.Canvas

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
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val sqFactor : Float = 11.2f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val deg : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n


fun Canvas.drawLineJoinSqRight(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val sq : Float = Math.min(w, h) / sqFactor
    save()
    translate(w / 2 + (w / 2 + size) * dsc(4), h / 2)
    rotate(deg * dsc(3))
    for (j in 0..1) {
        save()
        scale(1f, 1f - 2 * j)
        translate(0f, (size / 2 - sq / 2) * (1 - dsc(2)))
        drawRect(RectF(0f, -sq / 2, sq * dsc(1), sq / 2), paint)
        restore()
    }
    drawLine(0f, -size * 0.5f * dsc(0), 0f, size * 0.5f * dsc(0), paint)
    restore()
}

fun Canvas.drawLJSRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineJoinSqRight(scale, w, h, paint)
}
