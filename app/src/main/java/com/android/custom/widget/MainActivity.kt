package com.android.custom.widget

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class MainActivity : AppCompatActivity() {
    val mTVTestAnimal by lazy { findViewById<TextView>(R.id.mTVTestAnimal) }
    val mTestAnimal by lazy { findViewById<LottieAnimationView>(R.id.mTestAnimal) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTestAnimal.imageAssetsFolder="images"
        mTestAnimal.setAnimation("images/json_and_pic.json")
        mTVTestAnimal.setOnClickListener {
            mTestAnimal.playAnimation()
        }
    }

}