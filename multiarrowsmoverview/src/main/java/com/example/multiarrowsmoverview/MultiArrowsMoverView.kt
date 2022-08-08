package com.example.multiarrowsmoverview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
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
val parts : Int = 3
val scGap : Float = 0.02f / parts
val lines : Int = 4
val sizeFactor : Float = 18.2f
val delay : Long = 20
val deg : Float = 30f
val backColor : Int = Color.parseColor("#BDBDBD")
val strokeFactor : Float = 90f