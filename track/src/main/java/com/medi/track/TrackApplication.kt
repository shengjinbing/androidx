package com.medi.track

import android.app.Application
import com.medi.track.screen.SensorsDataAPI

/**
 * Created by lixiang on 2020/4/6
 * Describe:
 */
open class TrackApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initSensorsDataAPI(this)
    }

    fun initSensorsDataAPI(application:Application){
        SensorsDataAPI.init(application)
    }
}