package com.ttjjttjj.mybaselib.utils

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

/**
 * 封装了ViewBinding的使用
 *
 * 在Activity中： private val binding: ActivityMainBinding by inflate()
 *
 * https://mp.weixin.qq.com/s/GWQg4n5CFjuTfJu-AKu7aQ
 */

inline fun <reified VB : ViewBinding> Activity.inflate() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> Dialog.inflate() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) =
    VB::class.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB

//inline fun <reified T> getGenericType() = T::class.java

