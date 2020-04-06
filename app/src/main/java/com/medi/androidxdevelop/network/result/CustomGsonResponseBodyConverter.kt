package com.medi.comm.network.result

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.medi.comm.network.exception.NetException
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets.UTF_8


class CustomGsonResponseBodyConverter<T>(var gson: Gson, var adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun convert(value: ResponseBody): T {
        // 自定义解析(先解析code、message)
        value.use { value ->
            val response = value.string()
            val httpStatus = gson.fromJson(response, ResultIntercept::class.java)
            if (httpStatus.isCodeInvalid()) {
                value.close()
                throw NetException(httpStatus.code, httpStatus.msg)
            }
            val contentType = value.contentType()
            val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
            val inputStream = ByteArrayInputStream(response.toByteArray())
            val reader = InputStreamReader(inputStream, charset)
            val jsonReader = gson.newJsonReader(reader)

            return adapter.read(jsonReader)
        }
    }
}