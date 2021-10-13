package com.ttjjttjj.mybaselib.kotlintest.demo

class LazyDemo {

    private val name by lazy { "111" }
    private val name2 by lazy (LazyThreadSafetyMode.SYNCHRONIZED){ "111" }
    private val name3 by lazy ("11"){ "111" }



}