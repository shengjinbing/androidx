package com.medi.androidxdevelop.mvvm.Entity

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
data class BaseResponse<T>(
    var code: Int = 0,
    var msg: String? = "",
    var data: T
)