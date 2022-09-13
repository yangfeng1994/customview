package com.android.custom.widget

import android.opengl.Matrix
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.yf.widget.CyclingScoreView

class MainActivity : AppCompatActivity() {
    val mTVTestAnimal by lazy { findViewById<TextView>(R.id.mTVTestAnimal) }
    val mTestAnimal by lazy { findViewById<LottieAnimationView>(R.id.mTestAnimal) }
    val mChorno by lazy { findViewById<Chronometer>(R.id.mChorno) }
    val mScore by lazy { findViewById<CyclingScoreView>(R.id.mScore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTestAnimal.imageAssetsFolder = "images"
        mTestAnimal.setAnimation("images/json_and_pic.json")
        mTVTestAnimal.setOnClickListener {
            mTestAnimal.playAnimation()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mChorno.isCountDown = true
        }
        mChorno.base = SystemClock.elapsedRealtime().plus(120000)
        mChorno.setOnChronometerTickListener {
            Log.e("yyyyy", "it    ${it.base.minus(SystemClock.elapsedRealtime()).div(1000)}")
        }
        mChorno.start()
    }

}