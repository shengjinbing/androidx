package com.medi.daggerdemo.di.component

import com.medi.daggerdemo.MainActivity
import com.medi.daggerdemo.di.scope.ActivityScope
import com.medi.daggerdemo.login.LoginActivity
import com.medi.daggerdemo.login.LoginPasswordFragment
import com.medi.daggerdemo.login.LoginUsernameFragment
import dagger.Component
import dagger.Subcomponent

/**
 * Created by lixiang on 2020/7/28
 * Describe:
 */
@ActivityScope
@Subcomponent
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(loginActivity: LoginActivity)
    fun inject(usernameFragment: LoginUsernameFragment)
    fun inject(passwordFragment: LoginPasswordFragment)

}