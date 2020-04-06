package com.medi.comm.network.result

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


class CustomGsonConverterFactory(gson: Gson) : Converter.Factory(){
    lateinit var gson: Gson
    init {
        if (gson == null) throw NullPointerException("gson == null")
        this.gson = gson
    }
    companion object {
        fun create(): CustomGsonConverterFactory {
            return create(Gson())
        }
        fun create(gson: Gson): CustomGsonConverterFactory {
            return CustomGsonConverterFactory(gson)
        }
    }
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return CustomGsonResponseBodyConverter(gson, adapter)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return CustomGsonRequestBodyConverter(gson, adapter)
    }
}