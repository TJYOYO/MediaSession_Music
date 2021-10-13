package com.ttjjttjj.mybaselib.ext.util

import android.view.View
import com.ttjjttjj.mybaselib.ext.extensions.logw

/**
 * ClickExt.kt 主要支持多个View设置点击事件, 以及设置防止重复点击事件
 */

/**
 * 1 - 设置点击事件, 支持多个view - normal
 * @param views 需要设置点击事件的view
 * @param onClick 点击触发的方法
 */
fun setOnClick(vararg views: View?, onClick: (View) -> Unit) {
    views.forEach {
        it?.setOnClickListener { view ->
            onClick.invoke(view)
        }
    }
}

/**
 * 2 - 设置防止重复点击事件, 支持同时多个view
 * @param views 需要设置点击事件的view集合
 * @param interval 时间间隔 默认0.5秒
 * @param onClick 点击触发的方法
 *
 * 例如：
 * setOnclickNoRepeat(holder.mNameTv， holder.mAgeTv) {
 *      rvClickListener.onClick(it, position)
 * }
 */
fun setOnclickNoRepeat(vararg views: View?, interval: Long = 500, onClick: (View) -> Unit) {
    views.forEach {
        it?.clickNoRepeat(interval = interval) { view ->
            onClick.invoke(view)
        }
    }
}

/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */
var lastClickTime = 0L
fun View.clickNoRepeat(interval: Long = 500, action: (view: View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            "重复点击....".logw()
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}


