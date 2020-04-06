package com.medi.comm.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.medi.comm.network.interceptor.MockInterceptor
import com.medi.comm.network.interceptor.SignParamsInterceptor
import com.medi.comm.network.result.CustomGsonConverterFactory
import com.medi.comm.network.ssl.TrustAllCerts
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val signParamsInterceptor = SignParamsInterceptor.Builder()
        .addHeaderParam(key = "token", value = {
               "" })
        .build()

// 日志拦截器
val logIntercept = (if (false) HttpLoggingInterceptor().setLevel(Level.NONE) else HttpLoggingInterceptor().setLevel(Level.BODY))!!
// Gson转换器
val gsonConverterFactory = CustomGsonConverterFactory.create()
// coroutines
val coroutinesCallAdapter = CoroutineCallAdapterFactory()
// ssl
val sslSocketFactory = TrustAllCerts.createSSLSocketFactory()!!
// 通用okhttp client
val okHttpClient = OkHttpClient.Builder()
        //.sslSocketFactory(sslSocketFactory,TrustAllCerts())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        //.addInterceptor(signParamsInterceptor)
        .addInterceptor(logIntercept)
        //.enableTls12OnPreLollipop()
        .build()!!

// 通用retrofit设置
val commonRetrofitBuild = Retrofit.Builder()
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(coroutinesCallAdapter)
        .client(okHttpClient)!!

val baseRetrofit: Retrofit by lazy {
        commonRetrofitBuild
                .baseUrl("https://test-api-yj.mwcare.cn/")
                .build()
}

var BASE_URL = "http://test-api-yj.mwcare.cn/"
