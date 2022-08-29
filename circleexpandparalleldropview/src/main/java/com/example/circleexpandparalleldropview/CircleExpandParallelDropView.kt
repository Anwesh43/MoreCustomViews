package com.example.circleexpandparalleldropview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
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
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 16.2f
val gapFactor : Float = 4.2f
val delay : Long = 20
val backColor : Int = Color.parseColor("#bdbdbd")
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float =  Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawCircleExpandParallelDrop(scale : Float, w : Float, h : Float, paint : Paint) {
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    val r : Float = Math.min(w, h) / sizeFactor
    val gap : Float = Math.min(w, h) / gapFactor
    drawXY(w / 2,  h / 2) {
        rotate(rot * dsc(2))
        for (j in 0..1) {
            save()
            translate((h / 2 + r) * dsc(3 + j), (gap - r) * dsc(1) * (1f - 2 * j))
            drawArc(RectF(0f, -r, r,  2 * r), 0f, 360f * dsc(0), true, paint)
            restore()
        }
    }
}

fun Canvas.drawCEPDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    drawCircleExpandParallelDrop(scale, w, h, paint)
}
