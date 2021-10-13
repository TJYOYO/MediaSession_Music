package com.ttjjttjj.mybaselib.kotlintest.concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.locks.ReentrantLock

/**
 * launch 和 async 是可以创建协程，返回值不同，不会阻塞外部的线程或者协程
 *
 * withContext 是切换线程，并挂起外部协程，等内部执行完成，才能继续，可以看作阻塞了外部的线程或者协程(所以多个withcontext在一个launch协程中处理是串行的)
 *
 * withContext, async{}.await() = job.await(), delay() 几个函数都是挂起外部协程的，阻塞了
 */

/**
 * 通过CoroutineScope(Dispatchers.IO).launch{}创建协程，他们在不同子线程执行，并且不会阻塞诛仙传
 *
结果显示：主线程内容先执行，两个协程+IO，会在3个子线程异步的执行，下面我们希望其中的执行体task是同步的怎麽来？

currentThread:main, time:1631949431058, 方法start
currentThread:main, time:1631949431166, 方法end

currentThread:DefaultDispatcher-worker-3 @coroutine#3, time:1631949431176, start
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631949431182, start
currentThread:DefaultDispatcher-worker-2 @coroutine#1, time:1631949431182, start

currentThread:DefaultDispatcher-worker-3 @coroutine#3, time:1631949432176, end
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631949432182, end
currentThread:DefaultDispatcher-worker-2 @coroutine#1, time:1631949432183, end
 */
fun testByLaunch() {
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")
    // 重复执行3次，模拟点击3次
    repeat(3) {
        CoroutineScope(Dispatchers.IO).launch {
            task()
        }
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}

// ----------------------------------------------------------------- @Synchronzied 注解 实现同步-------------------------
/**
 * 测试协程中使用 @Synchronzied 注解, 同步执行体task
 *
 *
结果：显示下面的三个子线程中执行体是同步执行的，

currentThread:main, time:1631949586241, 方法start
currentThread:main, time:1631949586315, 方法end

currentThread:DefaultDispatcher-worker-2 @coroutine#2, time:1631949586318, start
currentThread:DefaultDispatcher-worker-2 @coroutine#2, time:1631949587318, end
currentThread:DefaultDispatcher-worker-3 @coroutine#3, time:1631949587318, start
currentThread:DefaultDispatcher-worker-3 @coroutine#3, time:1631949588318, end
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631949588319, start
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631949589319, end

 */
fun testCoroutineWithSync() {
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")
    repeat(3){
        CoroutineScope(Dispatchers.IO).launch {
            taskSynchronize()
        }
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}

/**
 * 执行体是taskSynchronizeByDelay(), 内部会使用delay函数，导致他外部的线程挂起，其他线程可以访问执行体，
 *
 * 所以：@Synchronized 同步注解，尽量不用修饰suspend的函数
 */
fun testCoroutineWithSync2() {
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")
    repeat(3){
        CoroutineScope(Dispatchers.IO).launch {
            taskSynchronizeByDelay()
        }
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}


// ----------------------------------------------------------------- job，join() 实现同步 -------------------------
/**
 * 使用job，join()实现多个 Coroutine的同步
 *
 * 结果：多个协程的执行体同步了，并且阻塞了主线程的执行
currentThread:main @coroutine#1, time:1631950934279, 方法start
currentThread:DefaultDispatcher-worker-2 @coroutine#2, time:1631950934307, start
currentThread:DefaultDispatcher-worker-2 @coroutine#2, time:1631950935307, end
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631950935309, start
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631950936309, end
currentThread:DefaultDispatcher-worker-2 @coroutine#4, time:1631950936309, start
currentThread:DefaultDispatcher-worker-2 @coroutine#4, time:1631950937310, end
currentThread:main @coroutine#1, time:1631950937310, 方法end
 *
 */
fun testSyncByJob() = runBlocking{
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")
    repeat(3){
        var job = CoroutineScope(Dispatchers.IO).launch {
            task()
        }
        job.start()
        job.join()
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}


// --------------------------------------------------------------ReentrantLock ---------------------
/**
 * ReentrantLock 重入锁，实现同步
 *
currentThread:main @coroutine#1, time:1631960884403, 方法start
currentThread:main @coroutine#1, time:1631960884445, 方法end
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631960884445, start
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631960885446, end
currentThread:DefaultDispatcher-worker-5 @coroutine#4, time:1631960885446, start
currentThread:DefaultDispatcher-worker-5 @coroutine#4, time:1631960886446, end
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631960886446, start
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631960887447, end
 */
fun testReentrantLock2() = runBlocking {

    val lock = ReentrantLock()
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")
    repeat(3){
        CoroutineScope(Dispatchers.IO).launch {
            lock.lock()
            task()
            lock.unlock()
        }
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}

// ---------------------------------------------------------- Mutex 实现同步-----------------------------------
/**
 * 使用协程的mutex同步
 *
 * 结果发现：同步完成
currentThread:main @coroutine#1, time:1631951230155, 方法start
currentThread:main @coroutine#1, time:1631951230178, 方法end
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631951230178, start
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631951231178, end
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631951231183, start
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631951232183, end
currentThread:DefaultDispatcher-worker-1 @coroutine#4, time:1631951232183, start
currentThread:DefaultDispatcher-worker-1 @coroutine#4, time:1631951233184, end
 *
 */
fun testSyncByMutex() = runBlocking {
    var mutex = Mutex()
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")
    repeat(3){
        CoroutineScope(Dispatchers.IO).launch {
            mutex.withLock {
                task()
            }
        }
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}

//--------------------------------------------------         async 创建协程 -------------------
/**
 * async启动协程，但是默认在主线程所以是顺序执行
 *
 * CoroutineScope.async 等同 CoroutineScope.launch，不同是返回值
 *
 * async加了Dispatchers.IO的异步调度，然后就是并行了
 *
 *  单个异步任务使用CoroutineScope.withContext
 * 多个异步任务使用CoroutineScope.async
 *

currentThread:main @coroutine#1, time:1631957324981, 方法start
currentThread:main @coroutine#1, time:1631957325007, 方法end
currentThread:DefaultDispatcher-worker-1 @coroutine#3, time:1631957325007, start
currentThread:DefaultDispatcher-worker-2 @coroutine#2, time:1631957325007, start
currentThread:DefaultDispatcher-worker-4 @coroutine#4, time:1631957325007, start
currentThread:DefaultDispatcher-worker-1 @coroutine#3, time:1631957326007, end
currentThread:DefaultDispatcher-worker-4 @coroutine#4, time:1631957326007, end
currentThread:DefaultDispatcher-worker-2 @coroutine#2, time:1631957326007, end
 */
fun testByCoroutineAsync() {
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")
    repeat(3){
        CoroutineScope(Dispatchers.IO).async {
            task()
        }
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")
    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10000)
}

/**
 *  async(Dispatchers.IO) {}.await() 中调用了await()，又让多个异步变成的串行
 *
 *  等同于上面的withContext(Dispatchers.IO)
 */
fun testByCoroutineAsync3() = runBlocking{
    repeat(3){
        async(Dispatchers.IO) {
            task()
        }.await()
    }
}

private fun task(){
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, start")
    Thread.sleep(1000)
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, end")
}

/**
 * @Synchronized 修改普通函数ok，可以同步
 */
@Synchronized
private fun taskSynchronize(){
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, start")
    Thread.sleep(1000)
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, end")
}

/**
 * 和方法taskSynchronize(), 不同的是内部使用了delay的挂起函数，而其它会阻塞，需要等它完成后面的才能开始
 *
 * @Synchronized 关键字不要修饰方法中有suspend挂起函数，因为内部又挂起了，就不会同步了
 */
@Synchronized
suspend fun taskSynchronizeByDelay(){
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, start")
    delay(1000)
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, end")
}



// -------------------------------------------------------  使用 withContext ------------------------------

/**
 * 单个withContext的异步任务
 *
currentThread:main @coroutine#1, time:1631958195591, 方法start
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958195669, start
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958196669, end
currentThread:main @coroutine#1, time:1631958196671, 方法end
 */
fun testByWithContext() = runBlocking {
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")

    withContext(Dispatchers.IO) {
        task()
    }

    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10 *1000)
}

/**
 * 结果显示：withContext两个任务，本身就是前后顺序执行，
 *
currentThread:main @coroutine#1, time:1631958027834, 方法start
repeat it = 0
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958027870, start
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958028870, end
repeat it = 1
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958028873, start
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958029873, end
repeat it = 2
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958029874, start
currentThread:DefaultDispatcher-worker-1 @coroutine#1, time:1631958030874, end
currentThread:main @coroutine#1, time:1631958030874, 方法end
 *
 * 为什么？withContext加了Dispatchers.IO的线程调度，不是异步的吗，怎麽还是同步的执行？
 *
 * 因为withContext 这个挂起函数，挂起了外部的协程，需要等它执行完成才能再执行,
 *
 * withContext 适合在需要自动切换线程更新UI时使用，不然推荐launch或者async方法
 */
fun testByWithContext2() = runBlocking {
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法start")

    repeat(3) {
        println("repeat it = $it")
        withContext(Dispatchers.IO) {
            task()
        }
    }

    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, 方法end")

    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10 *1000)
}

/**
 * 和上面不同加了CoroutineScope(Dispatchers.IO).launch {}包裹withContext外面
 *
 * 显示：异步
launch --- io
repeat it = 0
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631952344216, start
currentThread:main @coroutine#1, time:1631952344220, start
repeat it = 2
currentThread:DefaultDispatcher-worker-3 @coroutine#4, time:1631952344222, start
repeat it = 1
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631952344233, start
currentThread:DefaultDispatcher-worker-1 @coroutine#2, time:1631952345216, end
currentThread:DefaultDispatcher-worker-3 @coroutine#4, time:1631952345222, end
currentThread:DefaultDispatcher-worker-2 @coroutine#3, time:1631952345233, end
 */
suspend fun testByWithContext3() = runBlocking{
    println("launch --- io")

        repeat(3) {
            // 加了异步的就不会同步了
            CoroutineScope(Dispatchers.IO).launch {
                println("repeat it = $it")
                withContext(Dispatchers.IO) {
                    task()
            }
        }
    }
    println("currentThread:${Thread.currentThread().name}, time:${System.currentTimeMillis()}, start")
    // 防止main函数执行结束，就不管其他线程的打印工作了，哈哈
    Thread.sleep(10 *1000)
}