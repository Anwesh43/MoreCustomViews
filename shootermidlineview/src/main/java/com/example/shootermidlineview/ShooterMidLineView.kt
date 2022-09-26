package com.example.shootermidlineview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

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
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 180f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawShooterMidLine(scale : Float, w : Float, h : Float, paint : Paint) {
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val size : Float = Math.min(w, h) / sizeFactor
    drawXY(w / 2, h / 2) {
        drawXY(0f, (h / 2) * dsc(2)) {
            rotate(rot * dsc(1))
            drawLine(0f, 0f, 0f, -size * dsc(0) * 0.5f, paint)
        }
        for (j in 0..1) {
            drawXY(0f, (-h / 2) * dsc(3)) {
                drawLine(0f, 0f, 0f, -size * dsc(0), paint)
            }
        }
    }
}

fun Canvas.drawSMLNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawShooterMidLine(scale, w, h, paint)
}

class ShooterMidLineView(ctx : Context) : View(ctx) {

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