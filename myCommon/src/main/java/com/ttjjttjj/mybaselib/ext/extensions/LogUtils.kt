package com.ttjjttjj.mybaselib.ext.extensions

import android.util.Log


const val TAG = "logTest"

/**
 * log扩展，省事去写TAG
 */
fun Any.logDebug(msg : String) {
    Log.d(this.javaClass.simpleName, msg)
}

fun Any.logDebug(tag : String, msg : String) {
    Log.d(tag, msg)
}

fun Any.logError(msg : String) {
    Log.e(this.javaClass.simpleName, msg)
}

fun Any.logIgnore(msg : String) {
    Log.i(this.javaClass.simpleName, msg)
}

private enum class LEVEL {
    V, D, I, W, E
}

fun String.logv(tag: String = TAG) = log(LEVEL.V, tag, this)

fun String.logd(tag: String = TAG) = log(LEVEL.D, tag, this)

fun String.logi(tag: String = TAG) = log(LEVEL.I, tag, this)

fun String.logw(tag: String = TAG) = log(LEVEL.W, tag, this)

fun String.loge(tag: String = TAG) = log(LEVEL.E, tag, this)

private fun log(level: LEVEL, tag: String, message: String) {
    when (level) {
        LEVEL.V -> Log.v(tag, message)
        LEVEL.D -> Log.d(tag, message)
        LEVEL.I -> Log.i(tag, message)
        LEVEL.W -> Log.w(tag, message)
        LEVEL.E -> Log.e(tag, message)
    }
}
