package com.medi.comm.network.exception

import android.widget.Toast
import com.medi.androidxdevelop.base.ApplicationContext
import com.medi.androidxdevelop.network.NetworkUtils


/**
 * 网络异常处理
 */
class NetException(var code: Int, message: String) : RuntimeException(message) {
    fun composeException(afterFiltering: (code: Int, message: String) -> Unit) {
        composeException(afterFiltering, false)
    }

    fun composeException(afterFiltering: (code: Int, message: String) -> Unit, ignoreToast: Boolean = false) {
        var hasInvoked=false
        when (code) {
            else -> {
                try {
                    if (!ignoreToast && !NetExceptionCode.interceptCodes.contains(code)) {
                        Toast.makeText(ApplicationContext.application,message ?: "",Toast.LENGTH_LONG).show()
                    }else{
                        hasInvoked=true
                        afterFiltering(code,message ?:"网络异常，请稍后再试")
                    }
                    //LogUtils.e(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        if(!hasInvoked){
            if (NetworkUtils.isConnected()) {
                afterFiltering(code, message ?: "")
            } else {
                afterFiltering(code, if (!ignoreToast) "网络异常，请稍后再试" else "")
            }

        }
    }

}

object NetExceptionCode {
    @JvmStatic
    var HTTP_ERROR = 10000
    var HTTP_No_Net_ERROR = 100000
    @JvmStatic
    var interceptCodes = intArrayOf(3037, 3041, 3047, 3050, 3093, 2032, 3052, 2027, 5003, 5100, 3094,9998)
}

