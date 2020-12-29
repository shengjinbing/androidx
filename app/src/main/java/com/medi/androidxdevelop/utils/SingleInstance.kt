package com.medi.androidxdevelop.utils

import android.content.Context

/**
 * Created by lixiang on 2020/8/11
 * Describe:
 */
object SingleInstance {

    var helper = Helper("张三")

    private lateinit var context:Context

    fun setContext(cxt:Context){
        context = cxt
    }
}

class Helper(var name:String) {
}
