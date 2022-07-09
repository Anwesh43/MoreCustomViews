package com.example.alternatelinetobarview

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Paint
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
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val deg : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
val barSizeFactor : Float = 12.3f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawAlternateLineToBar(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sc1 : Float = scale.divideScale(0, parts)
    val sc2 : Float = scale.divideScale(1, parts)
    val sc3 : Float = scale.divideScale(2, parts)
    val sc4 : Float = scale.divideScale(3, parts)
    val barSize : Float = Math.min(w, h) / barSizeFactor
    save()
    translate(w / 2, h / 2 + (h / 2 + size) * sc4)
    rotate(deg * sc3)
    for (j in 0..1) {
        save()
        scale(1f - 2 * j, 1f - 2 * j)
        save()
        translate((w / 2) * (1 - sc1), barSize)
        drawLine(0f, 0f, size, 0f, paint)
        restore()
        drawRect(RectF(0f, 0f, size * sc2, barSize), paint)
        restore()
    }
    restore()
}

fun Canvas.drawALTBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawAlternateLineToBar(scale, w, h, paint)
}

class AlternateLineToBarView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}