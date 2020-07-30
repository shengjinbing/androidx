package com.medi.daggerdemo.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.medi.daggerdemo.R
import com.medi.daggerdemo.base.MyApplication
import com.medi.daggerdemo.di.component.LoginComponent
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    lateinit var loginComponent: LoginComponent

    @Inject lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginComponent =
            (applicationContext as MyApplication).appComponent.loginComponent().create()
        loginComponent.inject(this)

        setContentView(R.layout.activity_login)

        initData()
    }


    private fun initData(){
        val login = loginViewModel.getLoginViewModel()
        val userLocalDataSource = loginViewModel.userRepository.localDataSource.getUserLocalDataSource()
        Log.d("BBBBB","${login}")
        Log.d("BBBBB","${userLocalDataSource}")

    }
}