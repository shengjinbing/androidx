package com.medi.daggerdemo.di

/**
 * Created by lixiang on 2020/7/28
 * Describe:
 */

/*Android 中有两种主要的依赖项注入方式：
构造函数注入。这就是上面描述的方式。您将某个类的依赖项传入其构造函数。
字段注入（或 setter 注入）。某些 Android 框架类（如 Activity 和 Fragment）由系统实例化，因此无法进行
构造函数注入。使用字段注入时，依赖项将在创建类后实例化。代码如下所示：*/

class Car {
    lateinit var engine: Engine

    fun start() {
        engine.start()
    }
}

class Engine{
    fun start(){}
}

fun main() {
    val car = Car()
    car.engine = Engine()
    car.start()
}