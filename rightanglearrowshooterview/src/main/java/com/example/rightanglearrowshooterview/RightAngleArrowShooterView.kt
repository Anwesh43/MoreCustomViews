package com.example.rightanglearrowshooterview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
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
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val deg : Float = 90f
val sizeFactor : Float = 5.9f
val strokeFactor : Float = 90f
val arrowFactor : Float = 13.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawRightAngleArrowShooter(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sc1 : Float = scale.divideScale(0, parts)
    val sc2 : Float = scale.divideScale(1, parts)
    val sc3 : Float = scale.divideScale(2, parts)
    val sc4 : Float = scale.divideScale(3, parts)
    val arrowSize : Float = size / arrowFactor
    save()
    translate(w / 2, h / 2)
    for (j in 0..1) {
        save()
        rotate(deg * j * sc2)
        translate(0f, (-(h / 2) * (1f - j) - (w / 2) * j) * sc4)
        drawLine(0f, 0f, 0f, -size * sc1, paint)
        for (i in 0..1) {
            save()
            translate(0f, -size)
            rotate(deg * 0.5f * (1f - 2 * i))
            drawLine(0f, 0f, 0f, arrowSize * sc3, paint)
            restore()
        }
        restore()
    }
    restore()
}

fun Canvas.drawRAASNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawRightAngleArrowShooter(scale, w, h, paint)
}

class RightAngleArrowShootView(ctx : Context) : View(ctx) {

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