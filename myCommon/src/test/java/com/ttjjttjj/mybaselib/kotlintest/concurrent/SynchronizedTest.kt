package com.ttjjttjj.mybaselib.kotlintest.concurrent

import java.lang.Thread.sleep

/**
 * @Synchronized 注解进行同步
 */
@Synchronized
private fun postResult(s: String){
    println("${System.currentTimeMillis()} ：$s" )
    sleep(5000)
}


fun testSynchronized() {
    Thread{
        postResult("first thread")
    }.start()

    Thread{
        postResult("second thread")
    }.start()

    synchronized(Any()) {
        Thread {

        }
    }
    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}
