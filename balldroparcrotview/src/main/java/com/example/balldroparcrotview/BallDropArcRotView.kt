package com.example.balldroparcrotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

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
val delay : Long = 20
val sizeFactor : Float = 4.9f
val rot : Float = 180f
val deg : Float = 90f
val bakcColor : Int = Color.parseColor("#BDBDBD")
val scGap : Float = 0.04f / parts

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int,  n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n: Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawBallDropArcRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2) * dsc(3), h / 2) {
        rotate(deg * dsc(2))
        drawXY(0f, -(h / 2 + size / 2) * (1 - dsc(0))) {
            drawArc(RectF(-size / 2, -size / 2, size / 2, size / 2), rot * dsc(1), 360 - rot * dsc(1), true, paint)
        }
    }
}

fun Canvas.drawBDARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawBallDropArcRot(scale, w, h, paint)
}
