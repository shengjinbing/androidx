package com.medi.androidxdevelop.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.R
import kotlinx.android.synthetic.main.activity_kotlin.*
import java.io.File

/**
 * kotlin
 * 1.说一下kotlin的优缺点。let和with的区别
 * 2.扩展函数
 * 3.kotlin的lateinit和by lazy的区别
 *   1.lazy{} 只能用在val类型, lateinit 只能用在var类型
 *   2.lateinit不能用在可空的属性上和java的基本类型上
 *   3.当属性用到的时候才会初始化”lazy{}”里面的内容
 *   4.而且再次调用属性的时候，只会得到结果，而不会再次执行lazy{}的运行过程
 * 4.构造函数有哪几种
 *   1.主构造函数跟在类后面
 *   2.次构造函数不限
 * 5.协程
 */
class KotlinActivity : AppCompatActivity() {
    private var select = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
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
        with(tv_text){

        }
        //可以进行null判断
        tv_text?.run {

        }

        //通过it取对象，如果我们不想覆盖外部作用域的this，这时候去使用T.let会更加的方便
        tv_text?.let {
            print(it.text)
        }

        //从上面两段代码可以看出T.let和T.also的返回值使不同的。T.let返回的是作用域中的最后一个对象，它的值和类型都可以改变
        // 。但是T.also不管调用多少次返回的都是原来的original对象。
        val original = "abc"
        original.let {
            println("The original String is $it") // "abc"
            it.reversed()
        }.let {
            println("The reverse String is $it") // "cba"
            it.length
        }.let {
            println("The length of the String is $it") // 3
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