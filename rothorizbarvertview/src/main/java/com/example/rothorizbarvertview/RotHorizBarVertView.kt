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
val barHFactor : Float = 8.2f
val delay : Long = 20
val rot : Float = 180f
val deg : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")


