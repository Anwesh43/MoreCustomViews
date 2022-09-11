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
