package com.ttjjttjj.mybaselib.kotlintest.demo

import java.io.Serializable
import java.lang.reflect.ParameterizedType

/**
 * kotlin泛行使用
 */
// 泛行类 , out 只读
class GenericityDemo<out R> {


    // 泛型方法
    fun <T> printMiddle(vararg a:T) {

    }

    // 泛型方法，带泛型返回值
    fun <T> getMiddle(vararg a : T) :T {
        return a[a.size /2]
    }

    // 类型限定的泛型
    fun <T : Comparable<T>> compare(t1: T, t2 : T) : Int {
        return t1.compareTo(t2)
    }

    // 多个限定，使用where并抽出T，不同java是&
    fun <T> printArray(input: Array<T>) where T :Comparable<T>, T : Serializable {
        input.sort()
    }

    // 获取泛型T的实际类型
    fun getType(): String {
        return ((javaClass.genericSuperclass as ParameterizedType)).actualTypeArguments[0].toString()
    }




}