package com.ttjjttjj.mybaselib.base.application

import android.app.Application


open class BaseAPP: Application() {

    companion object {
        lateinit var appContext : Application
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }


}