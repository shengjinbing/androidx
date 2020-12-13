package com.medi.androidxdevelop.network

import com.medi.androidxdevelop.mvvm.Entity.BaseResponse
import com.medi.androidxdevelop.mvvm.Entity.FeeEntity
import com.medi.comm.network.baseRetrofit
import kotlinx.coroutines.Deferred
import retrofit2.http.POST

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
interface ApiService {

    @POST("/mgrdoctor/doctor/getConsultPrices")
    suspend fun getfee(): BaseResponse<MutableList<FeeEntity>>

    object apiService : ApiService by baseRetrofit.create(
        ApiService::class.java)
}

