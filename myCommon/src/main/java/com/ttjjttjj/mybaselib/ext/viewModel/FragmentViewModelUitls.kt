package com.ttjjttjj.mybaselib.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

fun <VM : ViewModel> Fragment.createViewModel(
    fragment: Fragment,
    factory: ViewModelProvider.Factory? = null,
    position: Int
): VM {
    val vbClass =
        (fragment.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()
    val viewModel = vbClass[position] as Class<VM>
    return factory?.let {
        ViewModelProvider(
            fragment,
            factory
        ).get(viewModel)
    } ?: let {
        ViewModelProvider(fragment).get(viewModel)
    }
}

fun <VM : ViewModel> Fragment.createActivityViewModel(
    fragment: Fragment,
    factory: ViewModelProvider.Factory? = null,
    position: Int
): VM {
    val vbClass =
        (fragment.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()
    val viewModel = vbClass[position] as Class<VM>
    return factory?.let {
        ViewModelProvider(
            fragment.requireActivity(),
            factory
        ).get(viewModel)
    } ?: let {
        ViewModelProvider(fragment.requireActivity()).get(viewModel)
    }
}