package com.medi.androidxdevelop.base

import android.app.Application
import com.medi.track.TrackApplication
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
class BaseApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        ApplicationContext.application = this
        ApplicationContext.context = this

        //ASM
        initASMSensorsDataAPI(this)
    }

    /**
     * 初始化埋点 SDK
     *
     * @param application Application
     */
    private  fun initASMSensorsDataAPI(application: Application) {
        SensorsDataAPI.init(application)
    }
}