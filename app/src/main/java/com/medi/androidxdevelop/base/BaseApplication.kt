package com.medi.androidxdevelop.base

import android.app.Application
import android.widget.Toast
import androidx.multidex.MultiDex
import com.github.moduth.blockcanary.BlockCanary
import com.medi.androidxdevelop.BuildConfig
import com.medi.androidxdevelop.leakcanary.LeakUploader
import com.meituan.android.walle.WalleChannelReader
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import com.tencent.mmkv.MMKV
import leakcanary.LeakCanary


/**
 *
 * https://www.jianshu.com/p/94e0f9ab3f1d
 * 1.一个应用程序有几个Context:
 * 其实这个问题本身并没有什么意义，关键还是在于对Context的理解，从上面的关系图我们已经可以得出答案了，
 * 在应用程序中Context的具体实现子类就是：Activity，Service，Application。那么Context
 * 数量=Activity数量+Service数量+应用进程数量。当然如果你足够细心，可能会有疑问：我们常说四大组件，
 * 这里怎么只有Activity，Service持有Context，那Broadcast Receiver，Content Provider呢？
 * Broadcast Receiver，Content Provider并不是Context的子类，他们所持有的Context都是其他地方传
 * 过去的，所以并不计入Context总数。上面的关系图也从另外一个侧面告诉我们Context类在整个Android系统中
 * 的地位是多么的崇高，因为很显然Activity，Service，Application都是其子类，其地位和作用不言而喻。
 * 2.这里我说一下上图中Application和Service所不推荐的两种使用情况。
 *   1：如果我们用ApplicationContext去启动一个LaunchMode为standard的Activity的时候会报错
 *   android.util.AndroidRuntimeException: Calling startActivity from outside of an
 *   Activity context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you
 *   want?这是因为非Activity类型的Context并没有所谓的任务栈，所以待启动的Activity就找不到栈了。解决这
 *   个问题的方法就是为待启动的Activity指定FLAG_ACTIVITY_NEW_TASK标记位，这样启动的时候就为它创建一个
 *   新的任务栈，而此时Activity是以singleTask模式启动的。所有这种用Application启动Activity的方式不推
 *   荐使用，Service同Application。
 *   2：在Application和Service中去layout inflate也是合法的，但是会使用系统默认的主题样式，
 *   如果你自定义了某些样式可能不会被使用。所以这种方式也不推荐使用。
一句话总结：凡是跟UI相关的，都应该使用Activity做为Context来处理；其他的一些操作，Service,Activity,Application等实例都可以，当然了，注意Context引用的持有，防止内存泄漏。
 */
class BaseApplication : Application() {
    init {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);
        ApplicationContext.application = this
        ApplicationContext.context = this

        initChannel()
        //ASM
        initASMSensorsDataAPI(this)
        //BlockCanary
        BlockCanary.install(this, AppBlockCanaryContext()).start()
        initLeakCanary()
        initXlog()
        initMmvk()
    }

    private fun initMmvk(){
        val rootDir = MMKV.initialize(this)
        println("mmkv root: $rootDir")
    }

    private fun initXlog() {
        val logPath = "${getExternalFilesDir(null)?.absolutePath}/marssample/log"
        android.util.Log.d("BBBBB",logPath)
        // cachePath这个参数必传，而且要data下的私有文件目录，例如 /data/data/packagename/files/xlog，
        // mmap文件会放在这个目录，如果传空串，可能会发生 SIGBUS 的crash。
        val cachePath = "${filesDir}/xlog"
        //init xlog
        /*var logConfig = Xlog.XLogConfig()
        logConfig.mode = Xlog.AppednerModeAsync
        logConfig.logdir = logPath
        logConfig.cachedir = cachePath
        logConfig.nameprefix = "logFileName"
        logConfig.pubkey = ""
        logConfig.compressmode = Xlog.ZLIB_MODE
        logConfig.compresslevel = 1
        logConfig.cachedays = 1*/
        Log.setLogImp(Xlog())
        if (BuildConfig.DEBUG) {
            Log.setConsoleLogOpen(true)
            Log.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, "", logPath, "logFileName", 0)
        } else {
            Log.setConsoleLogOpen(false)
            Log.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, "", logPath, "logFileName", 0)
        }
    }

    /**
     * 2.0版本之后不需要再进行配置
     * https://square.github.io/leakcanary/upgrading-to-leakcanary-2.0/
     */
    private fun initLeakCanary() {
        //配置LeakCanary，版本为1需要这样配置，2版本无需配置
        /* if (LeakCanary.isInAnalyzerProcess(this)) {
             return
         }
         LeakCanary.install(this)*/


        //您可以观看不再需要的任何对象，例如分离视图或损坏的演示者：
        //AppWatcher.objectWatcher.watch()
        //AppWatcher.config = AppWatcher.config.copy(watchFragmentViews = false)


        //禁用LeakCanary
        //LeakCanary.config.copy(dumpHeap = false)//禁用堆转储和分析
        //LeakCanary.config.copy(retainedVisibleThreshold = 1)
        //LeakCanary.showLeakDisplayActivityLauncherIcon(false)//隐藏泄漏显示活动启动器图标
        //配置上传到服务器的堆转储
        LeakCanary.config = LeakCanary.config.copy(onHeapAnalyzedListener = LeakUploader())
    }

    fun initChannel() {
        val channel = WalleChannelReader.getChannel(this.applicationContext)
        Toast.makeText(this, "channel==${channel}", Toast.LENGTH_SHORT).show()
        Log.d("BaseApplication", "channel==${channel}")
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