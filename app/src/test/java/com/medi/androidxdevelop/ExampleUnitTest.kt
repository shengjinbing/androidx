package com.medi.androidxdevelop

import androidx.arch.core.internal.SafeIterableMap
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import okhttp3.internal.wait
import org.junit.Test

import org.junit.Assert.*
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun main() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                /*withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000L)
                    println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                }*/
                //如果使用挂起函数需要使用withContext(NonCancellable)
                delay(1000)
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并等待它结束
        println("main: Now I can quit.")
    }

    /*@Test
    fun mainWithTimeout() = runBlocking {
        // withTimeout
        withTimeoutOrNull(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }
    }*/

    @Test
    fun mainSuspend() = runBlocking<Unit> {
        val time = measureTimeMillis {
            //使用 async 并发,可以提高速度
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }
            //等待
            println("The answer is ${one.await() + two.await()}")
            /* val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            println("The answer is ${one + two}")*/
        }
        println("Completed in $time ms")
    }

    /**
     *可选的，async 可以通过将 start 参数设置为 CoroutineStart.LAZY 而变为惰性的。 在这个模式下，只有结果通过
     * await 获取的时候协程才会启动，或者在 Job 的 start 函数调用的时候
     */
    @Test
    fun mainLay() = runBlocking<Unit> {
        val time = measureTimeMillis {
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
            // 执行一些计算
            one.start() // 启动第一个
            two.start() // 启动第二个
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // 假设我们在这里做了些有用的事
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // 假设我们在这里也做了一些有用的事
        return 29
    }

    //async 风格的函数
    suspend fun somethingUsefulOneAsync() = GlobalScope.async {
        doSomethingUsefulOne()
    }

    suspend fun somethingUsefulTwoAsync() = GlobalScope.async {
        doSomethingUsefulTwo()
    }

    @Test
    fun mainTest() = runBlocking<Unit> {
        val time = measureTimeMillis {
            println("The answer is ${concurrentSum()}")
        }
        println("Completed in $time ms")
    }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }

    /**
     * Dispatchers.Unconfined
     * Dispatchers.Default 默认调度器使用共享的后台线程池
     * Dispatchers.Main
     * Dispatchers.Io
     */
    @Test
    fun mainTest1() = runBlocking<Unit> {
        launch {
            // 运行在父协程的上下文中，即 runBlocking 主协程
            println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Unconfined) {
            // 不受限的——将工作在主线程中
            println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) {
            // 将会获取默认调度器
            println("Default               : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(newSingleThreadContext("MyOwnThread")) {
            // 将使它获得一个新的线程
            println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
        }
    }

    /**
     * 继承了runBlocking主协程的上下文，都是在主线程中协程,打印结果如下
     * [main @coroutine#2] I'm computing a piece of the answer
     *[main @coroutine#3] I'm computing another piece of the answer
     *[main @coroutine#1] The answer is 42
     */
    @Test
    fun mainTest2() = runBlocking<Unit> {
        val a = async(newSingleThreadContext("dasdas")) {
            log("I'm computing a piece of the answer")
            6
        }
        val b = async {
            log("I'm computing another piece of the answer")
            7
        }
        log("The answer is ${a.await() * b.await()}")
    }

    fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")


    /**
     * 切换协成上下文
     */
    @Test
    fun mainTest3() {
        newSingleThreadContext("Ctx1").use { ctx1 ->
            newSingleThreadContext("Ctx2").use { ctx2 ->
                runBlocking(ctx1) {
                    log("Started in ctx1")
                    withContext(ctx2) {
                        log("Working in ctx2")
                    }
                    log("Back to ctx1")
                }
            }
        }
    }

    /**
     * 子协成
     */
    @Test
    fun mainTest4() = runBlocking<Unit> {
        // 启动一个协程来处理某种传入请求（request）
        val request = launch {
            // 孵化了两个子作业, 其中一个通过 GlobalScope 启动
            GlobalScope.launch {
                println("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1: I am not affected by cancellation of the request")
            }
            // 另一个则承袭了父协程的上下文
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // 取消请求（request）的执行,
        //但是全局协成不会被取消
        delay(1000) // 延迟一秒钟来看看发生了什么
        println("main: Who has survived request cancellation?")
    }

    /**
     * 一个父协程总是等待所有的子协程执行结束
     */
    @Test
    fun mainTest5() = runBlocking<Unit> {
        // 启动一个协程来处理某种传入请求（request）
        val request = launch {
            repeat(3) { i ->
                // 启动少量的子作业
                launch() {
                    delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒的时间
                    println("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        request.join() // 等待请求的完成，包括其所有子协程
        println("Now processing of the request is complete")
    }


    @Test
    fun mainTest6() = runBlocking<Unit> {
        //组合上下文中的元素
        launch(Dispatchers.Default + CoroutineName("test")) {
            println("I'm working in thread ${Thread.currentThread().name}")
        }
    }


    fun foo(): Sequence<Int> = sequence {
        // 序列构建器
        for (i in 1..3) {
            Thread.sleep(100) // 假装我们正在计算
            yield(i) // 产生下一个值
        }
    }

    //序列器
    @Test
    fun mainTest7() {
        foo().forEach { value -> println(value) }
    }


    fun fooTest8(): Flow<Int> = flow {
        // 流构建器
        for (i in 1..3) {
            delay(100) // 假装我们在这里做了一些有用的事情
            emit(i) // 发送下一个值
        }
    }

    /**
     * 流的使用
     * 流使用 emit 函数 发射 值。
     * 流使用 collect 函数 收集 值。
     */
    @Test
    fun mainTest8() = runBlocking<Unit> {
        // 将一个整数区间转化为流
        (1..3).asFlow()
            .map { request -> performRequest(request) }//过度流操作
            .take(2) // 只获取前两个
            .collect { value -> println(value) }

        (1..3).asFlow() // 一个请求流
            .transform { request ->
                //转换操作
                emit("Making request $request")
                emit(performRequest(request))
            }
            .collect { response -> println(response) }

        val sum = (1..5).asFlow()
            .map { it * it } // 数字 1 至 5 的平方
            .reduce { a, b -> a + b } // 求和（末端操作符）
        println(sum)

        //流是连续的
        (1..5).asFlow()
            .filter {
                println("Filter $it")
                it % 2 == 0
            }
            .map {
                println("Map $it")
                "string $it"
            }.collect {
                println("Collect $it")
            }

        // 启动并发的协程以验证主线程并未阻塞
        launch {
            for (k in 1..3) {
                println("I'm not blocked $k")
                delay(100)
            }
        }
        // 收集这个流
        withTimeoutOrNull(250) {
            // 在 250 毫秒后超时
            fooTest8().collect { value -> println(value) }
        }
    }

    suspend fun performRequest(request: Int): String {
        delay(1000) // 模仿长时间运行的异步工作
        return "response $request"
    }

    //flowOn操作符可以切换流上下文
    fun fooFlow(): Flow<Int> = flow {
        for (i in 1..3) {
            Thread.sleep(100) // 假装我们以消耗 CPU 的方式进行计算
            log("Emitting $i")
            emit(i) // 发射下一个值
        }
    }.flowOn(Dispatchers.Default) // 在流构建器中改变消耗 CPU 代码上下文的正确方式


    //异常
    @Test
    fun mainTest9() = runBlocking<Unit> {
        try {
            fooTest9().collect { value ->
                println(value)
                check(value <= 1) { "Collected $value" }
            }
        } catch (e: Throwable) {
            println("Caught $e")
        }
    }


    fun fooTest9(): Flow<Int> = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i) // 发射下一个值
        }
    }


    fun fooTest10(): Flow<Int> = flow {
        emit(1)
        throw RuntimeException()
    }

    @Test
    fun mainTest10() = runBlocking<Unit> {
        fooTest10()
            .onCompletion { cause -> if (cause != null) println("Flow completed exceptionally") }
            .catch { cause -> println("Caught exception") }
            .collect { value -> println(value) }
    }


    // 模仿事件流
    fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

    //launchIn操作符号
    @Test
    fun mainTest11() = runBlocking<Unit> {
        events()
            .onEach { event -> println("Event: $event") }
            .launchIn(this) // <--- 在单独的协程中执行流
        println("Done")
    }

    /**
     * 通道基础
     * 一个 Channel 是一个和 BlockingQueue 非常相似的概念。其中一个不同是它代替了阻塞的 put 操作并提供了挂起的
     * send，还替代了阻塞的 take 操作并提供了挂起的 receive。
     */
    @Test
    fun mainTest12() = runBlocking {
        val channel = Channel<Int>()
        launch {
            // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
            for (x in 1..5) channel.send(x * x)
        }
        // 这里我们打印了 5 次被接收的整数：
        repeat(5) { println(channel.receive()) }
        println("Done!")
    }

/************************通道*************************/
    fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
        for (x in 1..Int.MAX_VALUE) send(x * x)
    }

    //构建通道生产者
    @Test
    fun mainTest13() = runBlocking {
        val squares = produceSquares()
        squares.consumeEach { println(it) }
        println("Done!")
    }


    //使用管道的素数
    @Test
    fun mainTest14() = runBlocking {
        var cur = numbersFrom(2)
        repeat(10) {
            val prime = cur.receive()
            println(prime)
            cur = filter(cur, prime)
        }
        coroutineContext.cancelChildren() // 取消所有的子协程来让主协程结束
    }

    fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
        var x = start
        while (true) send(x++) // 从 start 开始过滤整数流
    }

    fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
        for (x in numbers) if (x % prime != 0) send(x)
    }


    //扇出  多个协程也许会接收相同的管道，在它们之间进行分布式工作
    @Test
    fun mainT15() = runBlocking<Unit> {
        val producer = produceNumbers()
        repeat(5) { launchProcessor(it, producer) }
        delay(950)
        producer.cancel() // 取消协程生产者从而将它们全部杀死
    }

    fun CoroutineScope.produceNumbers() = produce<Int> {
        var x = 1 // start from 1
        while (true) {
            send(x++) // 产生下一个数字
            delay(100) // 等待 0.1 秒
        }
    }

    fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (msg in channel) {
            println("Processor #$id received $msg")
        }
    }

    data class Ball(var hits: Int)

    @Test
    fun mainTest16() = runBlocking {
        val table = Channel<Ball>() // 一个共享的 table（桌子）
        launch { player("ping", table) }
        launch { player("pong", table) }
        //激活
        table.send(Ball(0)) // 乒乓球
        delay(1000) // 延迟 1 秒钟
        coroutineContext.cancelChildren() // 游戏结束，取消它们
    }

    suspend fun player(name: String, table: Channel<Ball>) {
        for (ball in table) { // 在循环中接收球
            ball.hits++
            println("$name $ball")
            delay(300) // 等待一段时间
            table.send(ball) // 将球发送回去
        }
    }


    //属性委托
    //默认情况下，对于 lazy 属性的求值是同步锁的（synchronized）：该值只在一个线程中计算，并且所有线程会看到相同的值。
    // 如果初始化委托的同步锁不是必需的，这样多个线程可以同时执行，那么将 LazyThreadSafetyMode.PUBLICATION
    // 作为参数传递给 lazy() 函数。 而如果你确定初始化将总是发生在与属性使用位于相同的线程， 那么可以使用
    // LazyThreadSafetyMode.NONE 模式：它不会有任何线程安全的保证以及相关的开销。
    @Test
    fun mainTest17(){
      /*  val hashMapOf = SafeIterableMap<String,String>()
        hashMapOf.putIfAbsent("1","2")
        hashMapOf.forEach{
            hashMapOf.remove(it.key)
        }*/
        val p: String by Delegate()
        println("${p}")

        val lazyValue: String by lazy {
            println("computed!")
            "Hello"
        }
        println(lazyValue)
        println(lazyValue)

        var name: String by Delegates.observable("<no name>") {
                prop, old, new ->
            println("$old -> $new")
        }
        name = "first"
        name = "second"

    }

    class Delegate{
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return "$thisRef, thank you for delegating '${property.name}' to me!"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            println("$value has been assigned to '${property.name}' in $thisRef.")
        }
    }

}
