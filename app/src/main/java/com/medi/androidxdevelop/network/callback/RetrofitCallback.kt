package com.medi.comm.network.callback

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


/**
 * @name maikun
 * @class name：com.medi.comm.network.callback
 * @class describe
 * @author caichen QQ:345233199
 * @time 2020/3/19 23:09
 * @class describe
 */
interface RetrofitCallback<T> : Callback<T> {

    fun onSuccess(call: Call<T>?, response: Response<T>?)
    fun onLoading(total: Long, progress: Long)
    override fun onFailure(call: Call<T>, t: Throwable)

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful()) {
            onSuccess(call, response)
        } else {
            onFailure(call, Throwable(response.message()))
        }
    }
}