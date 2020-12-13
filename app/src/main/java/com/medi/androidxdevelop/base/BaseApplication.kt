package com.medi.androidxdevelop.base

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.BlockCanaryContext
import com.meituan.android.walle.WalleChannelReader
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

        initChannel()
        //ASM
        initASMSensorsDataAPI(this)
        //BlockCanary
        BlockCanary.install(this, BlockCanaryContext()).start()
    }

    fun initChannel(){
        val channel = WalleChannelReader.getChannel(this.applicationContext)
        Toast.makeText(this,"channel==${channel}",Toast.LENGTH_SHORT).show()
        Log.d("BaseApplication","channel==${channel}")
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