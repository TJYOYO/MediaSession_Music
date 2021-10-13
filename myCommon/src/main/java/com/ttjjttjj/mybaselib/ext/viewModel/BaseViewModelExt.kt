package com.ttjjttjj.mybaselib.ext.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ttjjttjj.mybaselib.ext.extensions.loge
import com.ttjjttjj.mybaselib.network.AppException
import com.ttjjttjj.mybaselib.network.base.BaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * 过滤服务器结果，失败抛异常
 * @param block 请求体方法，必须要用suspend关键字修饰, 返回BaseResponse<T>
 * @param success 成功回调, 返回空
 * @param error 失败回调 可不传， 默认{}
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> ViewModel.request(
    block: suspend () -> BaseResponse<T>,
    success: (T) -> Unit,
    error: (AppException) -> Unit = {},
    isShowDialog: Boolean = false,
    loadingMessage: String = "请求网络中..."
) : Job {
    // viewModelScope作用域launch创建协程
    return viewModelScope.launch {
        runCatching {
            block()
        }.onSuccess {

            //校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
            runCatching {
                executeResponse(it) {success(it)}
            }.onFailure {
                it.message?.loge()
                error(it.localizedMessage)
            }
        }.onFailure {
            //打印错误消息
            it.message?.loge()
            //失败回调
            error(it.localizedMessage)
        }
    }
}

suspend fun <T> executeResponse(
    response: BaseResponse<T>,
    success: suspend CoroutineScope.(T) -> Unit
) {
    coroutineScope {
        when(response.isSuccess()) {
            true -> success(response.getResponseData())
            false -> {
                throw AppException(response.getCode(), response.getResponseMsg())
            }
        }
    }
}