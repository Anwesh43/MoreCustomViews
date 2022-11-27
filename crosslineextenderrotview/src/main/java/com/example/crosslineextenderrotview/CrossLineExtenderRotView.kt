package com.example.crosslineextenderrotview

import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity

val colors : Array<Int> = arrayOf(
    "#AB47BC",
    "#1E88E5",
    "#00E678",
    "#FF3D00",
    "#3D5AFE"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val delay : Long = 20
val sizeFactor : Float = 4.9f
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val deg : Float = 45f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawCrossLineExtenderRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val size : Float = Math.min(w, h) / sizeFactor
    drawXY(w / 2, h / 2 - (h / 2 + size) * dsc(4)) {
        rotate(rot * dsc(3))
        for (j in 0..1) {
            drawXY(0f, 0f) {
                rotate((1 - deg) * dsc(2))
                drawLine(0f, -size * 0.5f * dsc(0), 0f, size * 0.5f * dsc(0), paint)
            }
        }
        drawLine(0f, 0f, size * dsc(1), 0f, paint)
    }
}

fun Canvas.drawCLERNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawCrossLineExtenderRot(scale, w, h, paint)
}
