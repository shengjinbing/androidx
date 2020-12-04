package com.medi.androidxdevelop.base

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.BlockCanaryContext
import com.medi.androidxdevelop.leakcanary.LeakUploader
import com.meituan.android.walle.WalleChannelReader
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import leakcanary.AppWatcher
import leakcanary.LeakCanary
import okhttp3.internal.wait

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationContext.application = this
        ApplicationContext.context = this

        val channel = WalleChannelReader.getChannel(this.applicationContext)
        Toast.makeText(this, "channel==${channel}", Toast.LENGTH_SHORT).show()
        Log.d("BaseApplication", "channel==${channel}")
        //ASM
        initASMSensorsDataAPI(this)
        //BlockCanary
        BlockCanary.install(this, BlockCanaryContext()).start()
        initLeakCanary()
    }

    /**
     * 2.0版本之后不需要再进行配置
     */
    private fun initLeakCanary() {
        //配置LeakCanary
       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)*/


        //您可以观看不再需要的任何对象，例如分离视图或损坏的演示者：
        //AppWatcher.objectWatcher.watch()

        //禁用LeakCanary
        //LeakCanary.config.copy(dumpHeap = false)//禁用堆转储和分析
        //LeakCanary.showLeakDisplayActivityLauncherIcon(false)//隐藏泄漏显示活动启动器图标

        //配置上传到服务器的堆转储
        //LeakCanary.config = LeakCanary.config.copy(onHeapAnalyzedListener = LeakUploader())
    }

    /**
     * 初始化埋点 SDK
     *
     * @param application Application
     */
    private fun initASMSensorsDataAPI(application: Application) {
        SensorsDataAPI.init(application)
    }


}