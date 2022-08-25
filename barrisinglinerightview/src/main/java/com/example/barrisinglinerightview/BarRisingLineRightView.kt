package com.example.barrisinglinerightview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.RectF
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
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val barHFactor : Float = 15.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawBarRisingLineRight(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val barH : Float = Math.min(w, h) / barHFactor
    val barW : Float = Math.min(w, h)
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    save()
    translate(w / 2 + (w / 2 + barW / 2) * dsc(4), h / 2)
    rotate(-rot * dsc(3))
    save()
    translate(0f, (h / 2 - barH) * (1 - dsc(2)))
    drawRect(RectF(-barW * 0.5f * dsc(0), 0f, barW * 0.5f * dsc(0), barH), paint)
    drawLine(0f, 0f, 0f, -size * dsc(1), paint)
    restore()
    restore()
}

fun Canvas.drawBRLRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBarRisingLineRight(scale, w, h, paint)
}
