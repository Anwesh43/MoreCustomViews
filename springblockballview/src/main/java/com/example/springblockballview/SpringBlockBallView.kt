package com.example.springblockballview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
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
val parts : Int = 7
val scGap : Float = 0.06f / parts
val sizeFactor : Float = 4.9f
val rot : Float = 180f
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

fun Canvas.drawSpringBlockBall(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    drawXY(w / 2 + (w / 2 + size / 2) * dsc(6), h / 2) {
        rotate(rot * dsc(3))
        drawXY(0f, -h / 2 -size + (h / 2 - size / 2) * (dsc(1) - dsc(5)) + (size / 2) * (dsc(2) - dsc(4))) {
            drawCircle(0f, 0f, size / 2, paint)
        }
        drawRect(RectF(-size / 2, -size * (dsc(0) - dsc(2) + dsc(4)), size / 2, 0f), paint)
    }
}

fun Canvas.drawSBBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawSpringBlockBall(scale, w, h, paint)
}