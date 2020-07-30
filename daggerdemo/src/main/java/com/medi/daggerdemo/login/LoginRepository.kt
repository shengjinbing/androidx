package com.medi.daggerdemo.login

import javax.inject.Inject

/**
 * Created by lixiang on 2020/7/29
 * Describe:
 */
class UserRepository @Inject constructor(
    public val localDataSource: UserLocalDataSource,
    public val remoteDataSource: UserRemoteDataSource
) {
}

class UserLocalDataSource @Inject constructor() {
    fun getUserLocalDataSource():Int{
        return 1
    }
}

class UserRemoteDataSource @Inject constructor(
    private val loginService: LoginRetrofitService
) {
    fun getUserRemoteDataSource():Int{
        return 2
    }
}