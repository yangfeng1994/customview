package com.android.custom.widget

import android.graphics.Typeface
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
import com.android.yf.widget.IntervalLineView
import com.android.yf.widget.StageSectionView

class MainActivity : AppCompatActivity() {
    val mTVTestAnimal by lazy { findViewById<TextView>(R.id.mTVTestAnimal) }
    val mIntervalLineView by lazy { findViewById<IntervalLineView>(R.id.mIntervalLineView) }

    //    val mTestAnimal by lazy { findViewById<LottieAnimationView>(R.id.mTestAnimal) }
//    val mChorno by lazy { findViewById<Chronometer>(R.id.mChorno) }
//    val mScore by lazy { findViewById<CyclingScoreView>(R.id.mScore) }
    val mStageSection by lazy { findViewById<StageSectionView>(R.id.mStageSection) }
    val mTypeface by lazy { Typeface.createFromAsset(assets, "Roboto-Regular.ttf") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        mTestAnimal.imageAssetsFolder = "images"
//        mTestAnimal.setAnimation("images/json_and_pic.json")
        mTVTestAnimal.setOnClickListener {
            mStageSection.postInvalidate()
        }
        mStageSection.setTypeFace(mTypeface)
        mStageSection.mCustomFtp = 120
        mStageSection.postInvalidate()
        thread()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            mChorno.isCountDown = true
//        }
//        mChorno.base = SystemClock.elapsedRealtime().plus(120000)
//        mChorno.setOnChronometerTickListener {
//            Log.e("yyyyy", "it    ${it.base.minus(SystemClock.elapsedRealtime()).div(1000)}")
//        }
//        mChorno.start()
    }

    private fun thread() {
        Thread {
            while (true) {
                Thread.sleep(1000)
                val random = (0..300).random()
                mStageSection.setPowerOutPut(random)
                val progress = (0..100).random()
                mIntervalLineView.setProgress(progress)
            }
        }.start()
    }

}