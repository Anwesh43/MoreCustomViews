package com.example.bilinealtarcrotview

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
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val arcFactor : Float = 11.2f
val delay : Long = 20
val deg : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y: Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}
fun Canvas.drawBiLineAltRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val r : Float = Math.min(w, h) / arcFactor
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(3)) {
        rotate(deg * dsc(2))
        drawXY(-(w / 2 + size / 2) * (1 - dsc(1)), 0f) {
            drawLine(-size / 2, 0f, size / 2, 0f, paint)
        }
        for (j in 0..1) {
            save()
            scale(1f - 2 * j, 1f - 2 * j)
            drawXY((size / 2  - r), 0f) {
                drawArc(RectF(-r, -r, r, r), 0f, 180f * dsc(1), true, paint)
            }
            restore()
        }
    }
}

fun Canvas.drawBLARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBiLineAltRot(scale, w, h, paint)
}

class BiLineAltArcRotView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}