package com.medi.comm.network.interceptor

import com.medi.androidxdevelop.network.request.FileResponseBody
import com.medi.comm.network.callback.RetrofitCallback
import okhttp3.Interceptor
import okhttp3.Response


/**
 * @author caichen QQ:345233199
 * @name maikun
 * @class name：com.medi.comm.network.interceptor
 * @class describe
 * @time 2020/3/19 23:02
 * @class describe
 */
class ProgressIntercept<T>(var callback: RetrofitCallback<T>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .body(FileResponseBody(originalResponse.body, callback))
            .build()
    }

}

