package com.example.squarelinetoarcview

import android.view.View
import android.view.MotionEvent
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Paint
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
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawSquareToLineArc(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2 + size) * dsc(4) , h / 2) {
        rotate(rot * dsc(3))
        drawXY(0f, (h / 2 + size / 2) * dsc(0)) {
            drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
        }
        drawXY(0f, -size / 2 - (h / 2 - size / 2) * (1 - dsc(1))) {
            for (j in 0..1) {
                drawXY(0f, 0f) {
                    rotate(rot * dsc(2) * (1 - 2 * j))
                    drawLine(0f, 0f, 0f, -size, paint)
                }
            }
            drawArc(
                RectF(-size / 2, -size / 2, size / 2, size / 2),
                180f + rot - rot * dsc(2),
                rot * 2 * dsc(2),
                true,
                paint
            )
        }
    }
}

fun Canvas.drawSLTANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawSquareToLineArc(scale, w, h, paint)
}
