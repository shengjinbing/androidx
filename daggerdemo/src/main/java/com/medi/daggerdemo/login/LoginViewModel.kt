package com.medi.daggerdemo.login

import com.medi.daggerdemo.di.scope.ActivityScope
import javax.inject.Inject

/**
 * Created by lixiang on 2020/7/28
 * Describe:
 */
@ActivityScope
class LoginViewModel @Inject constructor(
    public val userRepository: UserRepository
) {
    fun getLoginViewModel():Int{
        return 0
    }
}

