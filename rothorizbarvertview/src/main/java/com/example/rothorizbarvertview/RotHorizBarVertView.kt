package com.example.rothorizbarvertview

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val colors : Array<Int> = arrayOf(
    "#3f51b5",
    "#f44336",
    "#00897b",
    "#f50057",
    "#00e676"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 3.9f
val barHFactor : Float = 9.6f
val delay : Long = 20
val rot : Float = 180f
val deg : Float = 90f
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

fun Canvas.drawRotHorizBarVert(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val barH : Float = Math.min(w, h) / barHFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2 + size) * dsc(4)) {
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f, 1f - 2 * j)
                rotate(deg * dsc(2))
                drawRect(RectF(0f, 0f, size * dsc(0), barH), paint)
            }
        }
        drawXY(0f, 0f) {
            rotate(rot * dsc(1) - deg * dsc(2))
            drawLine(0f, 0f, 0f, barH * Math.floor(dsc(0).toDouble()).toFloat(), paint)
        }
    }
}

fun Canvas.drawRHBVNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawRotHorizBarVert(scale, w, h, paint)
}

class RotHorizBarVertView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}