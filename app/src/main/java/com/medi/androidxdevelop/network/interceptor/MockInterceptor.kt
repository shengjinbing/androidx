package com.medi.comm.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Description:
 *
 * @author guoyongping
 * @date   2020-02-08 11:27
 */
class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val requestUrl = request.url.toString()
        var mockUrl = "http://www.mocky.io/v2/58ddb1482800004b159e4b09"

        // 拦截咨询列表接口
        mockUrl = if (requestUrl.contains("/mgrdoctor/doctor/getDeletedPatients---------")) {
            "http://www.mocky.io/v2/5e5f3939310000b838afd9b8"
        } else if (requestUrl.contains("/mgrdoctor/doctor/doctorgrouppatient/getGroupById-------")){
            "http://www.mocky.io/v2/5e5f39cd3100008d15afd9c2"
        }else {
            requestUrl
        }
        request = request
            .newBuilder()
            .url(mockUrl)
            .build()

        return chain.proceed(request)
    }
}