package com.ttjjttjj.mybaselib.kotlintest

import com.ttjjttjj.mybaselib.kotlintest.concurrent.*
import kotlinx.coroutines.*
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun reentrantLockTest() {
        testReentrantLock()
    }

    @Test
    fun synchronizedTest() {
        testSynchronized()
    }

    /**
     * 协程的测试
     */
    @Test
    fun coroutineTest() {
        testByLaunch()
        //testByWithContext()
        //testByWithContext2()
        //testByCoroutineAsync()
        //testByCoroutineAsync3()
    }

    /**
     * 协程同步的测试
     */
    @Test
    fun mutexTest() {
        //testCoroutineWithSync()
        testCoroutineWithSync2()
        //testSyncByMutex()
        //testSyncByJob()
       // testReentrantLock2()
    }


    @Test
    fun joinTest() {
        //testJoin()
        testJoinSyncT1T2()
    }


}