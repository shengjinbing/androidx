package com.medi.androidxdevelop.mvvm.repository

import com.medi.androidxdevelop.mvvm.entity.BaseResponse
import com.medi.androidxdevelop.mvvm.entity.FeeEntity
import com.medi.androidxdevelop.network.ApiService

/**
 * Created by lixiang on 2021/1/14
 * Describe:
 */

class LoginRepository {
    suspend fun login(userName: String, password: String): BaseResponse<MutableList<FeeEntity>> {
        // 调用网络请求
        //将数据缓存到数据库
        return ApiService.apiService.getfee()
    }

    //从h缓存中获取数据
    fun getUserNameFormCache():String {
        return "张三"
    }
}