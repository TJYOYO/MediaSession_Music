package com.ttjjttjj.mybaselib.base.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

abstract class LifeActivity : AppCompatActivity() {

    val mTAG : String by lazy {
        javaClass.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(mTAG, "onCreate ---> ")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(mTAG, "onNewIntent ---> ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(mTAG, "onResume ---> ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(mTAG, "onPause ---> ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(mTAG, "onStop ---> ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(mTAG, "onDestroy ---> ")
    }

}