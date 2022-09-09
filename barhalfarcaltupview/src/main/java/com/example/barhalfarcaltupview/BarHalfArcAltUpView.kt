package com.example.barhalfarcaltupview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
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
val sizeFactor : Float = 4.9f
val delay : Long = 20
val rot : Float = 180f
val rFactor : Float = 19.2f
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

fun Canvas.drawScaleAt(scaleX : Float, scaleY : Float, cb : () -> Unit) {
    save()
    scale(scaleX, scaleY)
    cb()
    restore()
}

fun Canvas.drawBarHalfArcAltUp(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc  : (Int) -> Float = { scale.divideScale(it, parts) }
    val r : Float = Math.min(w, h) / rFactor
    drawXY(w / 2, h / 2) {
        for (j in 0..1) {
           drawScaleAt(1f - 2 * j, 1f - 2 * j) {
               drawXY(size * 0.5f * dsc(2), h * 0.5f * dsc(3)) {
                   drawRect(RectF(-r / 2, 0f, r / 2, size * 0.5f *  dsc(1)), paint)
                   drawArc(
                       RectF(-r / 2, -r / 2, r / 2, r / 2), 0f, rot * dsc(0),
                       true,
                       paint
                   )
               }
           }
        }
    }
}

fun Canvas.drawBHAAUNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBarHalfArcAltUp(scale, w, h, paint)
}

class BarHalfArcAltUpView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) :Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}