package com.example.biarrowshiftrotmoveview

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
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val deg : Float = 90f
val arrowSizeFactor : Float = 13.9f
val backColor : Int = Color.parseColor("#BDBDBD")
val parts : Int = 4
val scGap : Float = 0.04f / parts

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawBiArrowShiftRotMove(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sc1 : Float = scale.divideScale(0, parts)
    val sc2 : Float = scale.divideScale(1, parts)
    val sc3 : Float = scale.divideScale(2, parts)
    val sc4 : Float = scale.divideScale(3, parts)
    val arrowSize : Float = Math.min(w, h) / arrowSizeFactor
    save()
    translate(w / 2, h / 2)
    rotate(deg * sc3)
    for (j in 0..1) {
        save()
        translate((w / 2) * (1f - sc1) + (h / 2) * (1 - 2 * j) * sc4, arrowSize * (1f - 2 * j))
        drawLine(0f, 0f, size, 0f, paint)
        drawLine(0f, 0f, arrowSize * sc2, -arrowSize * sc2, paint)
        restore()
    }
    restore()
}

fun Canvas.drawBASRMNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBiArrowShiftRotMove(scale, w, h, paint)
}
