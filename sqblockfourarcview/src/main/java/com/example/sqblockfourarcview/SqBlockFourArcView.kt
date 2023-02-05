package com.example.sqblockfourarcview

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
val arcs : Int = 4
val parts : Int = 2 + arcs
val scGap : Float = 0.05f / parts
val strokeFactor : Float = 90f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val rot : Float = 90f
val sizeFactor : Float = 4.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawSqBlockFourArc(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2 + size) * dsc(5), h / 2) {
        rotate(rot * dsc(5))
        drawXY(-(w/ 2 + size / 2) * (1 - dsc(0)), 0f) {
            drawRect(
                RectF(-size / 2, -size / 2, size / 2, size / 2),
                paint
            )
        }
        for (j in 0..(arcs - 1)) {
            drawXY(0f, 0f) {
                rotate(rot * j)
                drawXY(size / 2, 0f) {
                    drawArc(
                        RectF(-size / 2, -size / 2, size / 2, size / 2),
                        -90f,
                        180f * dsc(j + 1),
                        true,
                        paint
                    )
                }
            }
        }
    }
}

fun Canvas.drawSBFANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawSqBlockFourArc(scale, w, h, paint)
}
