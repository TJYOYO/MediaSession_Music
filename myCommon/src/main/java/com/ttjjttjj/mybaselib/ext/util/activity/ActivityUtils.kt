package com.ttjjttjj.mybaselib.utils

import android.content.Context
import android.content.Intent

/**
 * 通过泛型实化 T::class.java 替代Activity的class对象
 *
 * 调用 ：startActivityByInline<LoginActivity>(this){
                putExtra("param1","data1")
                putExtra("param2","data2")
            }
 *
 * @param block 高阶函数
 */
inline fun <reified T> startActivityByInline(context : Context, block : Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    intent.block()
    context.startActivity(intent)
}

inline fun <reified T> startActivityByInlineTest(context : Context, block : (Intent) -> Unit) {
    val intent = Intent(context, T::class.java)
    //intent.block()
    block(intent)
    context.startActivity(intent)
}
