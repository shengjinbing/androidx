package com.medi.androidxdevelop.base

import com.medi.track.TrackApplication

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
class BaseApplication: TrackApplication() {
    override fun onCreate() {
        super.onCreate()
        ApplicationContext.application = this
        ApplicationContext.context = this

    }
}