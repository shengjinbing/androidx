package com.medi.androidxdevelop.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.medi.androidxdevelop.R
import kotlinx.android.synthetic.main.activity_kotlin.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * kotlin
 * 1.说一下kotlin的优缺点。let和with的区别
 * 2.扩展函数
 * 3.kotlin的lateinit和by lazy的区别
 *   1.lazy{} 只能用在val类型, lateinit 只能用在var类型
 *   2.lateinit不能用在可空的属性上和java的基本类型上
 *   3.当属性用到的时候才会初始化”lazy{}”里面的内容
 *   4.而且再次调用属性的时候，只会得到结果，而不会再次执行lazy{}的运行过程
 *   5.默认情况下，对于 lazy 属性的求值是同步锁的（synchronized）：该值只在一个线程中计算，并且所有线程会
 *   看到相同的值。如果初始化委托的同步锁不是必需的，这样多个线程可以同时执行，那么将
 *   LazyThreadSafetyMode.PUBLICATION 作为参数传递给 lazy() 函数。 而如果你确定初始化将总是发生在与
 *   属性使用位于相同的线程， 那么可以使用 LazyThreadSafetyMode.NONE 模式：它不会有任何线程安全的保证以
 *   及相关的开销。
 * 4.构造函数有哪几种
 *   1.主构造函数跟在类后面
 *   2.次构造函数不限
 *
 *
 * 5.协程 https://juejin.cn/post/6844904037586829320
 * 1.什么是协程
 * 协程是协作式任务, 线程是抢占式任务, 本质上两者都属于并发
 * Kotlin协程就是线程库不是协程? 内部代码用的线程池?
 * 最知名的协程语言Go内部也是维护了线程, 他也不是协程了?
 * 协程只是方便开发者处理异步, 线程才能提升性能效率, 两者本身不是替换关系没有说用了谁就不用另一个了
 * 2.协程是一种概念, 无关乎具体实现方式
 * kotlin标准库中的协程不包含线程池代码, 仅扩展库才内部处理了线程池
 * 3.协程设计来源
 * Kotlin的协程完美复刻了谷歌的Go语言的协程设计模式(作用域/channel/select), 将作用域用对象来具化出来; 且可以更好地控制作用域生命周期;
 * await模式(JavaScript的异步任务解决方案)
 * Kotlin参考RxJava响应式框架创造出Flow
 * 使用协程开始就不需要考虑线程的问题, 只需要在不同场景使用不同的调度器(调度器会对特定任务进行优化)就好
 * 协程优势
 * 并发实现方便
 * 没有回调嵌套发生, 代码结构清晰
 * 创建协程性能开销优于创建线程, 一个线程可以运行多个协程, 单线程即可异步
 *
 * 调用的两种方式
 * launch: 异步并发, 没有返回结果
 * async: 异步并发, 有返回结果
 *
 * 1.第一个参数 CoroutineContext上下文
 * 调度器Dispatchers继承自CoroutineContext, 该枚举拥有三个实现; 表示不同的线程调度; 当函数不使用调度器时承接当前作用域的调度器
 * Dispatchers.Unconfined 不指定线程, 如果子协程切换线程那么接下来的代码也运行在该线程上
 * Dispatchers.IO 适用于IO读写
 * Dispatchers.Main 根据平台不同而有所差, Android上为主线程
 * Dispatchers.Default 默认调度器, 在线程池中执行协程体, 适用于计算操作
 * 2.第二个参数 CoroutineStart
 *
 *
 *
 * 热数据通道Channel，实际上就是一个并发安全队列
 * 冷流数据Flow，Flow就是Kotlin协程与响应式编程模型结合的产物。所谓的冷流数据就是只有消费时才会产生的数据流，这一点和channel
 * 不正好相反，Channel的接收端不依赖与发送端
 *
 */
//协程的创建
/*public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    。。。省略代码。。。
    return coroutine
}*/


class KotlinActivity : AppCompatActivity() {
    private var select = true
    private lateinit var a :String//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        a = "null"
        init()
    }

    /**
     * run,with,let,also和apply这五个函数他们的用法及其相似
     * run,with,T.run,T.let中它们返回的都是作用域中最后一个对象(this);而T.apply和T.also返回值是调用者本身(itself)。
     * with,T.run,T.apply接收者是this;而T.let和T.also接受者是it
     */
    private fun init(){
        //作用域函数
        run {
            val b = select
            if(b) tv_text else tv_text1
        }.text = "李想"

        //with(T)函数，而另一个则是使用了T.run函数
        var c = with(tv_text){
            6
        }
        //可以进行null判断
        var run = tv_text?.run {
            8
        }
        //通过it取对象，如果我们不想覆盖外部作用域的this，这时候去使用T.let会更加的方便
        var let= tv_text?.let {
            print(it.text)
            4
        }
        //自己作为返回值
        val str = "abc"
        var apply = str.apply {
        }

        //从上面两段代码可以看出T.let和T.also的返回值使不同的。T.let返回的是作用域中的最后一个对象，它的值和类型都可以改变
        // 。但是T.also不管调用多少次返回的都是原来的original对象。
        val original = "abc"
        val a = original.let {
            println("The original String is $it") // "abc"
            it.reversed()
        }
        val b = original.let {
            println("The reverse String is $it") // "cba"
            it.length
        }
        original.also {
            println("The original String is $it") // "abc"
            it.reversed()
        }.also {
            println("The reverse String is ${it}") // "abc"
            it.length
        }.also {
            println("The length of the String is ${it}") // "abc"
        }


    }
    //原始函数
    fun makeDir(path: String): File {
        val result = File(path)
        result.mkdirs()
        return result
    }
    //通过let和also的链式调用改进后的函数
    fun makeDir1(path: String) = path.let{ File(it) }.also{ it.mkdirs() }
}

/********************委托***********************************/
class ResourceDelegate<T> : ReadOnlyProperty<MyUI, T> {
    override fun getValue(thisRef: MyUI, property: KProperty<*>): T {
        return  "ss" as T
    }
}

class ResourceLoader<T>(id: ResourceID<T>) {
    operator fun provideDelegate(
        thisRef: MyUI,
        prop: KProperty<*>
    ): ReadOnlyProperty<MyUI, T> {
        checkProperty(thisRef, prop.name)
        // 创建委托
        return ResourceDelegate<T>()
    }

    private fun checkProperty(thisRef: MyUI, name: String) {

    }
}

class MyUI {
    private fun <T> bindResource(id: ResourceID<T>): ResourceLoader<T> {
        return ResourceLoader(id)
    }

    val image by bindResource(ResourceID.image_id)
    val text by bindResource(ResourceID.text_id)
}

class ResourceID<T>{
    companion object{
        var  image_id = ResourceID<String>()
        var  text_id = ResourceID<String>()
    }


}