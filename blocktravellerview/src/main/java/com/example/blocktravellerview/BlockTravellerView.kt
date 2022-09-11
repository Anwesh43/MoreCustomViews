package com.example.blocktravellerview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
import android.content.Context
import android.app.Activity

val colors : Array<Int> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
).map {
    Color.parseColor(it)
}.toTypedArray()
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val barHFactor : Float = 4.9f
val barWFactor : Float = 10.2f
val parts : Int = 5
val scGap : Float = 0.04f / parts
val rot : Float = 45f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawBlockTraveller(scale : Float, w : Float, h : Float, paint : Paint) {
    val barH : Float = Math.min(w, h) / barHFactor
    val barW : Float = Math.min(w, h) / barWFactor
    val dsc : (Int) -> Float = { scale.divideScale(it, parts) }
    save()
    translate(w / 2 + (w / 4) * dsc(2), h / 2 + (h / 4) * dsc(2) + (h/ 4 + barH / 2) * dsc(4))
    rotate(rot * (dsc(1) - dsc(3)))
    drawRect(RectF(-barW / 2, barH / 2 - barH * dsc(0), barW / 2, barH / 2), paint)
    restore()
}

fun Canvas.drawBTNode(i : Int, scale : Float, paint : Paint) {
    paint.color = colors[i]
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    drawBlockTraveller(scale, w, h, paint)
}

class BlockTravellerView(ctx : Context) : View(ctx) {

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