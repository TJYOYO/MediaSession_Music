package com.ttjjttjj.mybaselib.kotlintest.coroutine

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Test

@Test
fun test1() {
    CoroutineScope(Dispatchers.Main).launch{
        delay(500)     //延时500ms
        Log.e("TAG","1.执行CoroutineScope.... [当前线程为：${Thread.currentThread().name}]")
    }
    Log.e("TAG","2.BtnClick.... [当前线程为：${Thread.currentThread().name}]")
}
