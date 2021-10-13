package com.ttjjttjj.mybaselib.kotlintest.concurrent

import java.util.concurrent.locks.ReentrantLock

/**
 * You can edit, run, and share this code.
 * play.kotlinlang.org
 */

fun testReentrantLock() {

    val lock = ReentrantLock()
    var count = 0

    val thread1 = Thread(){
        lock.lock()

        for (i in 0..200){
            count ++
            println("count1 = $count")
        }
        println("${Thread.currentThread().name} : count:${count}")

        lock.unlock()
    }
    thread1.name = "name1"

    val thread2 = Thread{
        lock.lock()

        for (i in 0..200){
            count ++
            println("count2 = $count")
        }
        println("${Thread.currentThread().name} : count:${count}")

        lock.unlock()
    }

    thread2.name = "name2"

    thread1.start()
    thread2.start()

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}
