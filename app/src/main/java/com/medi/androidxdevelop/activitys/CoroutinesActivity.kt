package com.medi.androidxdevelop.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.medi.androidxdevelop.R
import kotlinx.android.synthetic.main.activity_coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*

/**
 * https://mp.weixin.qq.com/s/MkWKMKj3yRa0H20nb9qcag
 *通常协程有三种方式创建
 *1.使用runBlocking顶层函数来创建协程，这种方式是线程阻塞的，适用于单元测试，一般业务开发不会使用这种
 *2.使用GlobalScope单例对象，并且调用launch函数来创建协程，这种方式不会阻塞线程，但是不推荐在Android中使用这种方式，因为它的生命周期是整个应用程序的生命周期，如果处理不好，容易导致内存泄漏，而且不能取消
 *3.使用CoroutineScope对象，并且调用launch函数来创建协程，这种方式可以通过传入的CoroutineContext来控制协程的生命周期，推荐使用这种方式
 *
 * 在Android平台上，协程有助于解决两个主要问题：
 * 1.管理长时间运行的任务，如果管理不当，这些任务可能会阻塞主线程并导致你的应用界面冻结。
 * 2.提供主线程安全性，或者从主线程安全地调用网络或者磁盘操作。
 *
 * 编译器会在编译期间对被suspend修饰符修饰的函数进行续体传递风格（CPS）变换，它会改变suspend函数的函数签名，我举个例子：
   await函数是个suspend函数，函数签名如下所示：
   suspend fun <T> CompletableFuture<T>.await(): T
   在编译期间进行**续体传递风格（CPS）**变换后：
   fun <T> CompletableFuture<T>.await(continuation: Continuation<T>): Any?
   我们可以看到进行续体传递风格（CPS）变换后的函数多了一个类型为Continuation的参数，Continuation代码如下所示：
   interface Continuation<in T> {
     val context: CoroutineContext
     fun resumeWith(result: Result<T>)
   }
   续体包装了协程在挂起之后继续执行的代码，在编译过程中，一个完整的协程被分割成一个又一个续体，在await函数的挂起结束之后，
   它会调用参数continuation的resumeWith函数来恢复执行await之后的代码。
   进行续体传递风格（CPS）变换后的函数返回值是Any?，这是因为这个函数发生变换后，它会返回一个类型为T（返回它本身）和COROUTINE_SUSPENDED标记的联合类型，
   因为Kotlin没有联合类型语法，所以就使用最泛化的类型Any?来表示，COROUTINE_SUSPENDED标记表示的是这个suspend函数会发生事实上的挂起操作。
 */
class CoroutinesActivity : AppCompatActivity() {
     val TAG: String = "Coroutines_log"

    //1.创建一个MainScope
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines)
        lifecycleScope.launch {
            // 代表当前生命周期处于 Resumed 的时候才会执行(选择性使用)
            whenResumed {
                // ... 具体的协程代码
            }
        }

        //2.启动协程
        scope.launch(Dispatchers.Main) {
            Log.d(TAG, "开始")
            //创建一个协程，之后返回一个 Deferred<T>对象，我们可以调用 Deferred#await()去获取返回的值类似Java 中的Future
            //async 能够并发执行任务，执行任务的时间也因此缩短了一半
            //除了上述的并发执行任务，async 还可以对它的 start 入参设置成懒加载,这样系统就可以在调用它的时候再为它分配资源了。
            val one = async{ getResult(20) }
            Log.d(TAG, "one")
            val two = async { getResult(40) }
            Log.d(TAG, "two")
            tvNum.text = (one.await() + two.await()).toString()
            Log.d(TAG, "three")

        }
        initFlow()
        initChannel()
    }

    // 3. 销毁的时候释
    override fun onDestroy() {
        super.onDestroy()

        scope.cancel()
    }

    private suspend fun getResult(num: Int): Int {
        delay(5000)
        return num * num
    }

    private suspend fun getResult1(num: Int): Int {
        //可以切换到指定的IO线程去进行网络或数据库请求
        return withContext(Dispatchers.IO) {
            num * num
        }
    }

    /**
     * 改变数据发射的线程	flowOn	subscribeOn
     * 改变消费数据的线程	无	    observeOn
     *
     * 1.改变数据发射的线程
    flowOn 使用的参数是协程对应的调度器，它实质改变的是协程对应的线程。
    2.改变消费数据的线程
    我在上面的表格中并没有写到在 Flow 中如何改变消费线程，并不意味着 Flow 不可以指定消费线程？
    Flow 的消费线程在我们启动协程指定调度器的时候就确认好了，对应着启动协程的调度器。比如在上面的代码中 lifecycleScope 启动的调度器是 Dispatchers.Main，那么 collect 方法就消费在主线程。
     */
    private fun initFlow() {
        lifecycleScope.launch {
            //Log.d(TAG, Thread.currentThread().name+"1")
            // 创建一个协程 Flow<T>,collect 方法和 RxJava 中的 subscribe 方法一样，都是用来消费数据的
            val flow = createFlow()
                .onCompletion {
                    //Log.d(TAG, Thread.currentThread().name + "4")
                }
                // 将数据发射的操作放到 IO 线程中的协程
                .flowOn(Dispatchers.IO)

            //协作取消
           // withTimeoutOrNull(1600) {
                flow.collect { num ->
                    //Log.d(TAG, "$num")
                    //Log.d(TAG, Thread.currentThread().name+"3")
                }
            //}

        }
    }


    private fun createFlow(): Flow<Int> = flow {
        Log.d(TAG, Thread.currentThread().name + "2")
        for (i in 1..10)
            emit(i)
    }

    /**
     * 通道
     * Channel是一个面向多协程之间数据传输的 BlockQueue。它的使用方式超级简单：
     */
    private fun initChannel() {
        lifecycleScope.launch {
            // 1. 生成一个 Channel
            val channel = Channel<Int>()

            // 2. Channel 发送数据
            launch {
                for (i in 1..5) {
                    delay(200)
                    channel.send(i * i)
                }
                channel.close()
            }

            // 3. Channel 接收数据
            launch {
                for (y in channel)
                    Log.e(TAG, "get $y")
            }

            //通过扩展函数来
            val channel1 = produce<Int> {
                for (i in 1..5) {
                    delay(200)
                    send(i * i)
                }
                close()
            }

        }


        /**
         * https://mp.weixin.qq.com/s/AW80E3jIqddf5wKzVbDrBg
        协程是轻量级的线程，为什么是轻量的？可以先告诉大家结论，因为它基于线程池API，所以在处理并发任务这件事上它真的游刃有余。
        有可能有的同学问了，既然它基于线程池，那我直接使用线程池或者使用 Android 中其他的异步任务解决方式，比如 Handler、RxJava等，不更好吗？
        协程可以使用阻塞的方式写出非阻塞式的代码，解决并发中常见的回调地狱，这是其最大的优点，后面介绍。


        GlobalScope、Dispatcher 和 launch，他们分别对应着协程的作用域、调度器和协程构建器
        一、协程作用域
        协程的作用域有三种，他们分别是：
        1.runBlocking：顶层函数，它和 coroutineScope 不一样，它会阻塞当前线程来等待，所以这个方法在业务中并不适用 。
        2.GlobalScope：全局协程作用域，可以在整个应用的声明周期中操作，且不能取消，所以仍不适用于业务开发。
        3.自定义作用域：自定义协程的作用域，不会造成内存泄漏。
        显然，我们不能在 Activity 中调用 GlobalScope，这样可能会造成内存泄漏，看一下如何自定义作用域，具体的步骤我在注释中已给出：

        二.调度器
        调度器的作用是将协程限制在特定的线程执行。主要的调度器类型有：

        Dispatchers.Main：指定执行的线程是主线程，如上面的代码。
        Dispatchers.IO：指定执行的线程是 IO 线程。
        Dispatchers.Default：默认的调度器，适合执行 CPU 密集性的任务。
        Dispatchers.Unconfined：非限制的调度器，指定的线程可能会随着挂起的函数的发生变化。

        三.launch
        1.launch 的作用从它的名称就可以看的出来，启动一个新的协程，它返回的是一个 Job对象，我们可以调用 Job#cancel() 取消这个协程。
        除了 launch，还有一个方法跟它很像，就是 async，它的作用是创建一个协程，之后返回一个 Deferred<T>对象，我们可以调用 Deferred#await()去获取返回的值，有点类似于 Java 中的   Future
        2.那我们什么时候需要使用挂起函数呢？常见的场景有：
        耗时操作：使用 withContext 切换到指定的 IO 线程去进行网络或者数据库请求。
        等待操作：使用delay方法去等待某个事件。

        协作取消
        Flow 采用和协程一样的协作取消，也就是说，Flow 的 collect 只能在可取消的挂起函数中挂起的时候取消，否则不能取消。

        如果我们想取消 Flow 得借助 withTimeoutOrNull 之类的顶层函数，不妨猜一下，下面的代码最终会打印出什么？

        5. 操作符对比
        限于篇幅，我仅介绍一下 Flow 中操作符的作用，就不一一介绍每个操作符具体怎么使用了。

        普通操作符：
        Flow 操作符	作用
        map	转换操作符，将 A 变成 B
        take	后面跟 Int 类型的参数，表示接收多少个 emit 出的值
        filter	过滤操作符
        特殊的操作符
        总会有一些特殊的情况，比如我只需要取前几个，我只要最新的数据等，不过在这些情况下，数据的发射就是并发执行的。

        Flow 操作符	作用
        buffer	数据发射并发，collect 不并发
        conflate	发射数据太快，只处理最新发射的
        collectLatest	接收处理太慢，只处理最新接收的
        组合操作符
        Flow 操作符	作用
        zip	组合两个流，双方都有新数据才会发射处理
        combine	组合两个流，在经过第一次发射以后，任意方有新数据来的时候就可以发射，另一方有可能是已经发射过的数据
        展平流操作符
        展平流有点类似于 RxJava 中的 flatmap，将你发射出去的数据源转变为另一种数据源。

        Flow 操作符	作用
        flatMapConcat	串行处理数据
        flatMapMerge	并发 collect 数据
        flatMapLatest	在每次 emit 新的数据以后，会取消先前的 collect
        末端操作符
        顾名思义，就是帮你做 collect 处理，collect 是最基础的末端操作符。

        末端流操作符	作用
        collect	最基础的消费数据
        toList	转化为 List 集合
        toSet	转化为 Set 集合
        first	仅仅取第一个值
        single	确保流发射单个值
        reduce	规约，如果发射的是 Int，最终会得到一个 Int，可做累加操作
        fold	规约，可以说是 reduce 的升级版，可以自定义返回类型
        其他还有一些操作符，我这里就不一一介绍了，感兴趣可以查看 API。


         */
        fun main() {
            // 启动一个协程
            GlobalScope.launch(Dispatchers.Main) {
                delay(1000L)
                println("World!")
            }
        }

    }
}