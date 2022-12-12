package com.example.morecustomviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.lineextendbarrotview.LineExtendBarRotView

//import com.example.bilineparallelbarview.BiLineParallelBarView

//import com.example.circleattachedlineshooterview.CircleAttachedLineShooterView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        //.create(this)
        LineExtendBarRotView.create(this)
        fullScreen()
    }
}

fun MainActivity.fullScreen() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    actionBar?.hide()
    supportActionBar?.hide()
}