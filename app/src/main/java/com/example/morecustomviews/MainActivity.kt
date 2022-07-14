package com.example.morecustomviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.rightanglearrowshooterview.RightAngleArrowShootView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        RightAngleArrowShootView.create(this)
        fullScreen()
    }
}

fun MainActivity.fullScreen() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    actionBar?.hide()
    supportActionBar?.hide()
}