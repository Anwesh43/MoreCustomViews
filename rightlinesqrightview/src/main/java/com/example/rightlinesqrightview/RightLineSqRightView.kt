package com.example.rightlinesqrightview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Paint

val colors : Array<Int> = arrayOf(
    "#ef5350",
    "#534bae",
    "#e040fb",
    "#3d5afe",
    "#bc477b"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.9f
val rot : Float = 90f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
