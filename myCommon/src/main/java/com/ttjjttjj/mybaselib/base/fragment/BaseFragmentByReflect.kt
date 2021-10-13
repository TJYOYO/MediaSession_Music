package com.ttjjttjj.mybaselib.base.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.ttjjttjj.mybaselib.ext.extensions.logDebug
import com.ttjjttjj.mybaselib.network.manager.NetState
import com.ttjjttjj.mybaselib.network.manager.NetworkStateManager
import com.ttjjttjj.mybaselib.utils.createActivityViewModel
import com.ttjjttjj.mybaselib.utils.createViewModel
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
abstract class BaseFragmentByReflect<VB : ViewBinding, VM :ViewModel>( private val shareViewModel: Boolean = false,
                                                                       private val factory: ViewModelProvider.Factory? = null) : LifeFragment() {

    lateinit var mContext : Context
    lateinit var mViewBinding : VB
    lateinit var mViewModel: VM

    private val handler = Handler(Looper.getMainLooper())
    //是否第一次加载
    private var isFirst: Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewBinding = getViewBinding() as VB
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = when (shareViewModel) {
            true -> createActivityViewModel(this, factory, 1)
            false -> createViewModel(this, factory, 1)
        }
        initView(savedInstanceState)
        createObserver()
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * initView
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 创建观察者
     */
    abstract fun createObserver()

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {
        logDebug("网络变化监听...")


    }


    /**
     * 反射实现 var binding = FragmentLoginBinding.inflate(layoutInflater)
     */
    private fun getViewBinding(): ViewBinding {
        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
        val typeArguments = parameterizedType.actualTypeArguments
        var clazz = typeArguments[0] as Class<*>
        var method = clazz.getDeclaredMethod("inflate", LayoutInflater::class.java)
        return method.invoke(clazz, layoutInflater) as ViewBinding
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿
            handler.postDelayed( {
                lazyLoadData()
                //在Fragment中，只有懒加载过了才能开启网络变化监听
                NetworkStateManager.instance.mNetworkStateCallback.observeInFragment(
                    this,
                    Observer {
                        //不是首次订阅时调用方法，防止数据第一次监听错误
                        if (!isFirst) {
                            onNetworkStateChanged(it)
                        }
                    })
                isFirst = false
            },lazyLoadTime())
        }
    }

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    open fun lazyLoadTime(): Long {
        return 300
    }

}