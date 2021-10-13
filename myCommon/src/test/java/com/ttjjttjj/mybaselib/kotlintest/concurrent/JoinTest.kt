package com.ttjjttjj.mybaselib.kotlintest.concurrent

import java.lang.Thread.sleep

/**
 *
 * “ 现有 Task1、Task2 等多个并行任务，如何等待全部任务执行完成后，开始执行 Task3 ? ”
 * https://mp.weixin.qq.com/s/AJHO0nVT1jL9gpRR91dT-g
 *
 *
 *
 * 参考：有关join的使用 - https://www.cnblogs.com/paddix/p/5381958.html
 */

/**
 * 方法中T1,T2 中join和start没有对应一起，所以导致T1和T2还是并发的，但是他们是在T3前面完成的
 *
 * 结果：
 *
 *  task1 : start
    task2 : start
    task2 : finished
    task1 : finished
    task3 : start
    task3 finished: finished finished
 */
fun testJoin() {
    lateinit var s1: String
    lateinit var s2: String

    val t1 = Thread { s1 = task1() }
    val t2 = Thread { s2 = task2() }
    t1.start()
    t2.start()

    t1.join()
    t2.join()

    task3(s1, s2)

    sleep(10000)
}

/**
 * 方法中 join 跟在对应start之后才能保证同步了T1,T2任务，就是等T1执行完成，T2才开始，然后T3
 *
 * 这个方法正确！！！
 *
 * 结果：

task1 : start
task1 : finished
task2 : start
task2 : finished
task3 : start
task3 finished: finished finished

 */
fun testJoinSyncT1T2() {
    lateinit var s1: String
    lateinit var s2: String

    val t1 = Thread { s1 = task1() }
    val t2 = Thread { s2 = task2() }
    t1.start()
    t1.join()

    t2.start()
    t2.join()

    task3(s1, s2)

    sleep(10000)
}

private val task1: () -> String = {
    "start".also { println("task1 : $it") }
    sleep(2000)
    "finished".also { println("task1 : $it") }
}

val task2: () -> String = {
    "start".also { println("task2 : $it") }
    sleep(2000)
    "finished".also { println("task2 : $it") }
}

val task3: (String, String) -> String = { p1, p2 ->
    "start".also { println("task3 : $it") }
    sleep(2000)
    "$p1 $p2".also { println("task3 finished: $it") }
}