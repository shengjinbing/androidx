package com.medi.comm.network.result

import android.util.Log
import com.google.gson.Gson
import com.medi.androidxdevelop.network.NetworkUtils
import com.medi.comm.network.exception.NetException
import com.medi.comm.network.exception.NetExceptionCode.HTTP_ERROR
import com.medi.comm.network.exception.NetExceptionCode.HTTP_No_Net_ERROR
import kotlinx.coroutines.Deferred


suspend inline fun <reified T> Deferred<T>.awaitOrError(
    isShowErrToash: Boolean = false
): Result<T> {
    return try {
        val result = await()
        Log.d("BBBBB",result.toString())
        val resultIntercept = Gson().fromJson<ResultIntercept>(Gson().toJson(result))
        if (resultIntercept.isCodeInvalid()) {
            throw NetException(resultIntercept.code, resultIntercept.msg)
        } else {
            Result.of(result)
        }
    } catch (e: Exception) {
        Log.d("BBBBB",e.message)
        e.printStackTrace()
        if (e is NetException) {
            Result.of(e)
        } else {
            if (e is NetException) {
                Result.of(e)
            } else {
                Result.of(
                    NetException(
                        if (NetworkUtils.isConnected()) HTTP_ERROR else HTTP_No_Net_ERROR,
                        ""
                    )
                )
            }

        }
    }
}

inline fun <reified T> Gson.fromJson(json: String) = fromJson(json, T::class.java)
