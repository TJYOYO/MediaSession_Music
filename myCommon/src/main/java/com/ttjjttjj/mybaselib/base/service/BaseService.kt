package com.ttjjttjj.mybaselib.base.service

import android.app.Service
import com.ttjjttjj.mybaselib.exception.GlobalCoroutineExceptionHandler
import com.ttjjttjj.mybaselib.ext.extensions.NormalScope
import kotlinx.coroutines.*

abstract class BaseService :Service(){
    private val normalScope = NormalScope()

    override fun onDestroy() {
        normalScope.cancel()
        super.onDestroy()
    }

    protected fun requestMain(
        errCode: Int = -1, errMsg: String = "", report: Boolean = false,
        block: suspend CoroutineScope.() -> Unit) {
        normalScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
            block.invoke(this)
        }
    }


    protected fun requestIO(
        errCode: Int = -1, errMsg: String = "", report: Boolean = false,
        block: suspend CoroutineScope.() -> Unit): Job {
        return normalScope.launch(Dispatchers.IO + GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
            block.invoke(this)
        }
    }

    protected fun delayMain(
        delayTime: Long,errCode: Int = -1, errMsg: String = "", report: Boolean = false,
        block: suspend CoroutineScope.() -> Unit) {
        normalScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
            withContext(Dispatchers.IO) {
                delay(delayTime)
            }
            block.invoke(this)
        }
    }
}