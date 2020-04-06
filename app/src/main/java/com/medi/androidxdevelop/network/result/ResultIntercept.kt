package com.medi.comm.network.result


/**
 * 结果model
 */
class ResultIntercept(var code: Int, var msg: String){
    fun isCodeInvalid() :Boolean{
        return (code != 0)
    }
}