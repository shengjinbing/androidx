package com.medi.comm.network.interceptor

import android.text.TextUtils
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.util.*

/**
 * header拦截器
 */
class SignParamsInterceptor private constructor() : Interceptor {
    internal var queryParamsMap: MutableMap<String, String> = HashMap()
    internal var paramsMap: MutableMap<String, String> = HashMap()
    internal var headerParamsMap: MutableMap<String, () -> String> = HashMap()
    internal var headerLinesList: MutableList<String> = ArrayList()
    var interceptorUrl: ArrayList<String> = ArrayList()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        var request: Request? = chain.request()
        val requestBuilder = request!!.newBuilder()

        // process header params inject
        if (headerParamsMap.isNotEmpty()) {
            val keys = headerParamsMap.keys
            for (headerKey in keys) {
                requestBuilder.addHeader(headerKey, headerParamsMap[headerKey]!!.invoke()).build()
            }
        }

        val headerBuilder = request.headers.newBuilder()
        if (headerLinesList.size > 0) {
            for (line in headerLinesList) {
                headerBuilder.add(line)
            }
            requestBuilder.headers(headerBuilder.build())
        }
        // process header params end


        // process queryParams inject whatever it's GET or POST
        if (queryParamsMap.isNotEmpty()) {
            request = injectParamsIntoUrl(request.url.newBuilder(), requestBuilder, queryParamsMap)
        }

        request = requestBuilder.build()
        chain.proceed(request!!)
    }

    // func to inject params into url
    private fun injectParamsIntoUrl(httpUrlBuilder: HttpUrl.Builder, requestBuilder: Request.Builder, paramsMap: Map<String, String>): Request? {
        if (paramsMap.isNotEmpty()) {
            val iterator = paramsMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next() as Map.Entry<*, *>
                httpUrlBuilder.addQueryParameter(entry.key as String, entry.value as String)
            }
            requestBuilder.url(httpUrlBuilder.build())
            return requestBuilder.build()
        }

        return null
    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }

    }

    class Builder {

        private var interceptor:  SignParamsInterceptor = SignParamsInterceptor()

        fun setInterceptUrls(interceptorUrl: List<String>): Builder {
            interceptor.interceptorUrl.addAll(interceptorUrl)
            return this
        }

        fun addParam(key: String, value: String): Builder {
            interceptor.paramsMap[key] = value
            return this
        }

        fun addParamsMap(paramsMap: Map<String, String>): Builder {
            interceptor.paramsMap.putAll(paramsMap)
            return this
        }

        fun addHeaderParam(key: String, value: () -> String): Builder {
            interceptor.headerParamsMap!![key] = value
            return this
        }

        fun addHeaderParamsMap(headerParamsMap: Map<String, () -> String>): Builder {
            interceptor.headerParamsMap!!.putAll(headerParamsMap)
            return this
        }

        fun addHeaderLine(headerLine: String): Builder {
            val index = headerLine.indexOf(":")
            if (index == -1) {
                throw IllegalArgumentException("Unexpected header: $headerLine")
            }
            interceptor.headerLinesList.add(headerLine)
            return this
        }

        fun addHeaderLinesList(headerLinesList: List<String>): Builder {
            for (headerLine in headerLinesList) {
                val index = headerLine.indexOf(":")
                if (index == -1) {
                    throw IllegalArgumentException("Unexpected header: $headerLine")
                }
                interceptor.headerLinesList.add(headerLine)
            }
            return this
        }

        fun addQueryParam(key: String, value: String): Builder {
            interceptor.queryParamsMap[key] = value
            return this
        }

        fun addQueryParamsMap(queryParamsMap: Map<String, String>): Builder {
            interceptor.queryParamsMap.putAll(queryParamsMap)
            return this
        }

        fun build(): SignParamsInterceptor {
            return interceptor
        }
    }
}