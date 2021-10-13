package com.ttjjttjj.mybaselib.base.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import java.lang.Exception

/**
 * Activity 的基类，可以是模板
 *
 * 注意：
 * 继承 BaseViewBindingByReflect， 反射耗时20到200ms之间，正常几十, 运行后20ms内，优点：只要传对应Binding就行
 * 继承 BAViewBinding ，耗时10ms以内，缺点；要实现ViewBinding的回调
 */
abstract class BaseActivity: LifeActivity() {

    val mContext : Context
        get() = this@BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hideStatus()
        //hideActionBar()
    }

    abstract fun initObserve()

    /**
     * init view or click , longClick
     */
    abstract fun initView()

    /**
     * init first data
     */
    abstract fun initData()

    /**
     * 隐藏状态栏
     */
    fun hideStatus() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 隐藏标题栏
     */
    fun hideActionBar () {
        supportActionBar?.hide()
    }

    fun startActivityBySafe(cls : Class<*>) {
        try {
            startActivity(Intent(mContext, cls))
        }catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


}