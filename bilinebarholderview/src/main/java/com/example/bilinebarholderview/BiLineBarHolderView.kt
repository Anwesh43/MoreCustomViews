package com.example.bilinebarholderview

import android.view.View
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.view.MotionEvent

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
val rot : Float = 180f
val sizeFactor : Float = 4.9f
val barHFactor : Float = 11.2f
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

fun Canvas.drawBiLineHolder(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val barH : Float = Math.min(w, h) / barHFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    drawXY(w / 2, h / 2) {
        for (j in 0..1) {
            save()
            rotate(rot * j * dsc(3))
            drawXY(0f, -(h / 2) * dsc(2 * j + 2)) {
                drawLine(0f, 0f, 0f,-size * dsc(1), paint)
                drawXY(0f, -size * dsc(1)) {
                    drawRect(RectF(-size / 2, -barH * dsc(0), size / 2, 0f), paint)
                }
            }
            restore()
        }
    }
}

fun Canvas.drawBLHNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBiLineHolder(scale, w, h, paint)
}

class BiLineBarHolderView(ctx : Context) : View(ctx) {

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