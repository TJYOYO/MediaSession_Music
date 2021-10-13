对应的单元测试 - ExampleUnitTest

1： 锁分为对象锁和类锁

对象锁：锁 + 方法 ，锁 + 对象

类锁：锁 + 类，锁 + 静态方法

参考:
#聊一聊Kotlin中的线程安全
https://juejin.cn/post/6972373944267440164#heading-1


2： 同步锁的方式
Thread.join
Synchronized
ReentrantLock
BlockingQueue
CountDownLatch
CyclicBarrier
CAS
Future
CompletableFuture
Rxjava
Coroutine
Flow

参考：
#Kotlin 线程同步的 N 种方法
https://juejin.cn/post/6981952428786597902#heading-0

3：测试

看myCommon/test/java/com.ttjjttjj/mybaselib/kotlintest/concurrent 相关的代码

