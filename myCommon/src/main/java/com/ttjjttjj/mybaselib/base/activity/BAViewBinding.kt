package com.ttjjttjj.mybaselib.base.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * 泛型ViewBinding，然后抽象方法中每个Activity实现ViewBiding
 *
 * 相比较BaseViewBindingByReflect 获取到ViewBinding的耗时少一些
 */
@Suppress("UNCHECKED_CAST")
abstract class BAViewBinding<T : ViewBinding> : AppCompatActivity() {

    lateinit var viewBindingT : T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var time = System.currentTimeMillis()

        viewBindingT = getViewBinding() as T

        Log.d(TAG, "getViewBinding ---> time = " + (System.currentTimeMillis() - time))

        setContentView(viewBindingT.root)
    }

    /**
     * 耗时 32ms, 26ms, 11, 9 , 8, 8
     */
    abstract fun getViewBinding() : ViewBinding

    companion object {
        private const val TAG = "BAViewBinding"
    }

}