package com.ttjjttjj.mybaselib.utils


import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType


/**
 * Activity 根据position获取viewModel, 无参数时
 */
inline fun <VM: ViewModel> ComponentActivity.createViewModel(position:Int) : VM {

    val vbClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()
    Log.d("ViewModel", "vbClass = $vbClass") // [class com.ttjjttjj.myview.databinding.ActivityMainBinding, class com.ttjjttjj.myview.viewModel.MainViewModel]
    val viewModel = vbClass[position] as Class<VM>
    Log.d("ViewModel", "viewModel = $viewModel") // com.ttjjttjj.myview.viewModel.MainViewModel
    return ViewModelProvider(this).get(viewModel)
}

/**
 * 获取ViewModel，区分了有无参数factory的情况
 *
 * 上面对的封装，不一定比KTX提供的好
 * by viewModel { factory }
 * 
 */
fun <VM : ViewModel> ComponentActivity.createViewModel(
    activity: ComponentActivity,
    factory: ViewModelProvider.Factory? = null,
    position: Int
): VM {
    val vbClass =
        (activity.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()
    val viewModel = vbClass[position] as Class<VM>
    /**
     * 先判断factory是否为null的情况，通过ViewModelProvider load
     */
    return factory?.let {
        ViewModelProvider(
            activity,
            factory
        ).get(viewModel)
    } ?: let {
        ViewModelProvider(activity).get(viewModel)
    }
}

