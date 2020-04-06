package com.medi.comm.network.result

import com.medi.comm.network.exception.NetException


/**
 * 网络请求结果
 */
class Result<T> {
    var isCache=false
    var value: T? = null
        private set
    var error: NetException? = null
        private set
    fun initSuc(value:T):Result<T>{
        this.value=value
        return this
    }
    fun isCache(isCa:Boolean):Result<T>{
        isCache=isCa
        return this
    }
    fun initErr(error:NetException):Result<T>{
        this.error=error
        return this
    }
    operator fun component1(): T? {
        return value
    }

    operator fun component2(): NetException? {
        return error
    }

    companion object {
        fun <T> of(error: NetException): Result<T> {
            return Result<T>().initErr(error)
        }
        fun <T> of(value: T): Result<T> {
            return  Result<T>().initSuc( value)
        }
    }
}
