package com.medi.androidxdevelop.base

import android.app.Application

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationContext.application = this
        ApplicationContext.context = this

    }
}