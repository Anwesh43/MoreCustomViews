package com.example.sqabovearcrotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
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
val rot : Float = 90f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val sizeFactor : Float = 4.9f
val sqFactor : Float = 5.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawSqAboveRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val sqSize : Float = size / sqFactor
    drawXY(w / 2 + (w / 2 + size) * dsc(4), h / 2) {
        rotate(-rot * dsc(3))
        drawArc(RectF(-size, -size, size, size), 0f, 180f * dsc(0), true, paint)
        drawXY(-size + (size - sqSize / 2) * dsc(2), 0f) {
            drawRect(RectF(0f, -sqSize * dsc(1), sqSize, 0f), paint)
        }
    }
}

fun Canvas.drawSARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawSqAboveRot(scale,w , h, paint)
}

class SqAboveArcRotView(ctx : Context) : View(ctx) {

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