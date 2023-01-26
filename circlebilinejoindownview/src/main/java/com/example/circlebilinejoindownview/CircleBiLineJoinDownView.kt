package com.example.circlebilinejoindownview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.app.Activity
import android.content.Context
import android.graphics.RectF
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
val sizeFactor : Float = 4.9f
val rFactor : Float = 13.2f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val strokeFactor : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawCircleBiLineJoinDown(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val r : Float = Math.min(w, h) / rFactor
    drawXY(w / 2, h / 2 + (h / 2 + r) * dsc(3)) {
        drawCircle(0f, 0f, r * dsc(0), paint)
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f - 2 * j, 1f)
                rotate(rot * dsc(2))
                drawXY(r + (w / 2 - r) * (1 - dsc(1)), 0f) {
                    drawLine(0f, 0f, size, 0f, paint)
                }
            }
        }
    }
}

fun Canvas.drawCBLJDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawCircleBiLineJoinDown(scale, w, h, paint)
}
