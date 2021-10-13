package com.ttjjttjj.mybaselib.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.ttjjttjj.mybaselib.ext.extensions.logDebug
import com.ttjjttjj.mybaselib.utils.createViewModel
import java.lang.Exception
import java.lang.reflect.ParameterizedType

/**
 *
 * 通过反射对ViewBinding的封装，有20-300ms的耗时 , 对性能要求不高可以使用，不行就使用BAViewBinding.kt
 *
 * 参考： https://www.jianshu.com/p/e8c449887b49
 */
abstract class BAViewBindingByReflect<T : ViewBinding, VM : ViewModel> (private val factory: ViewModelProvider.Factory? = null): BaseActivity(){

    lateinit var viewBinding : T
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutView())
        //viewModel = createViewModel(1)
        viewModel = createViewModel(this, factory, 1)
        initView()
        initData()
        initObserve()
    }

    /**
     * 封装通过反射获取ViewBinding, 然后获取根view
     *
     * 这样所有的Activity中就不需要去对应的inflate得到ViewBinding了
     *
     * 耗时：197ms , 39ms, 9ms
     */
    private fun getLayoutView(): View? {
        var time = System.currentTimeMillis()

        val clazz = findViewBindingTypeClass(this) ?: throw Exception("ViewBinding class is null ...")

        viewBinding = getActivityViewBinding(clazz, layoutInflater) as T

        logDebug("getViewBinding ---> time = " + (System.currentTimeMillis() - time))
        return viewBinding.root
    }

    /**
     * 在类中找到 "带泛型的T" 对应的 Class
     */
    private fun findViewBindingTypeClass(any: Any) : Class<*>? {
        //得到Class
        var clazz = any.javaClass
        logDebug( "clazz= $clazz") // class com.ttjjttjj.myview.ui.activity.MainActivity

        // Type 包含了Class等多个类型的父类型
        var type = clazz.genericSuperclass
        logDebug( "genericSuperclass= $type") // com.ttjjttjj.mybaselib.baseActivity.BAViewBindingByReflect<com.ttjjttjj.myview.databinding.ActivityMainBinding, com.ttjjttjj.myview.viewModel.MainViewModel>

        // 属于参数化类型的Type，就是泛型的Type
        var parameterizedType = type as ParameterizedType
        logDebug("parameterizedType= $parameterizedType") // com.ttjjttjj.mybaselib.baseActivity.BAViewBindingByReflect<com.ttjjttjj.myview.databinding.ActivityMainBinding, com.ttjjttjj.myview.viewModel.MainViewModel>

        // 当前有多个泛型的Type[] 数组
        var arguments = parameterizedType.actualTypeArguments
        logDebug("actualTypeArguments= $arguments") // [Ljava.lang.reflect.Type;@66671dc

        if (arguments.isEmpty()) {
            return null
        }

        logDebug("actualTypeArguments[0]= ${arguments[0]}") //class com.ttjjttjj.myview.databinding.ActivityMainBinding

        // 第一个参数
        return arguments[0] as Class<*>
    }

    /**
     * 反射实现获取到ViewBinding：var binding = ActivityLoginBinding.inflate(layoutInflater)
     *
     */
    private fun getActivityViewBinding(
        viewBindingClass: Class<*>,
        layoutInflater: LayoutInflater
    ): ViewBinding {
        var methodInflate = viewBindingClass.getMethod("inflate", LayoutInflater::class.java)
        return methodInflate.invoke(viewBindingClass, layoutInflater) as ViewBinding
    }

    companion object {
        private const val TAG = "BAViewBindingByReflect"
    }


}