package com.medi.comm.network.result

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.Charset

class CustomGsonRequestBodyConverter<T>(gson: Gson, adapter: TypeAdapter<T>) : Converter<T,RequestBody>{
    private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    private val UTF_8 = Charset.forName("UTF-8")

    private var gson: Gson
    private var adapter: TypeAdapter<T>

    init {
        this.gson = gson
        this.adapter = adapter
    }
    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

}