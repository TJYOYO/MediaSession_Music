package com.ttjjttjj.mybaselib.kotlintest

/**
 * https://juejin.cn/post/6844903590545326088#heading-5
 */

// 饿汉式 - 内部静态直接初始化，不管是否使用，这是饿了吗，哈哈
object TestSingleton {
}

/*对应java
//Java实现
public class SingletonDemo {
    private static SingletonDemo instance=new SingletonDemo();
    private SingletonDemo(){

    }
    public static SingletonDemo getInstance(){
        return instance;
    }
}*/





// 懒汉式 - 线程安全， 使用时每次都要线程安全处理，懒懒的，推脱吗，哈哈
class TestSingletonDemo {

    companion object {
        private var instance: TestSingletonDemo ?= null
            get() {
                if (field == null) {
                    field = TestSingletonDemo()
                }
                return field
            }

        @Synchronized
        fun get() : TestSingletonDemo {
            return instance!!
        }
    }
}

/*//Java实现
public class SingletonDemo {
    private static SingletonDemo instance;
    private SingletonDemo(){}
    public static synchronized SingletonDemo getInstance(){//使用同步锁
        if(instance==null){
            instance=new SingletonDemo();
        }
        return instance;
    }
}*/






// 双重检索 Double Check
class DoubleCheckSingleton {
    companion object {
        val instance: DoubleCheckSingleton by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DoubleCheckSingleton()
        }
    }
}

/*//Java实现
public class SingletonDemo {
    private volatile static SingletonDemo instance;
    private SingletonDemo(){}
    public static SingletonDemo getInstance(){
        if(instance==null){
            synchronized (SingletonDemo.class){
                if(instance==null){
                    instance=new SingletonDemo();
                }
            }
        }
        return instance;
    }
}*/





// 静态内部类
class StaticInnerSingleton private constructor(){

    companion object {
        var instance = SingletonHolder.staticInnerSingleton
    }

    private object SingletonHolder {
        var staticInnerSingleton = StaticInnerSingleton()
    }

}

/*
//Java实现
public class SingletonDemo {
    private static class SingletonHolder{
        private static SingletonDemo instance=new SingletonDemo();
    }
    private SingletonDemo(){
        System.out.println("Singleton has loaded");
    }
    public static SingletonDemo getInstance(){
        return SingletonHolder.instance;
    }
}
*/


