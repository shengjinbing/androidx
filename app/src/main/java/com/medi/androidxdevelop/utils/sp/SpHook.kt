package com.medi.androidxdevelop.utils.sp

import android.os.Build
import android.os.Handler
import android.os.Message
import com.medi.androidxdevelop.utils.RefInvoke
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by lixiang on 2021/2/5
 * Describe:
 */
class SpHook {
    companion object{
        fun tryHackActivityThreadH() {
            try {
                if (Build.VERSION.SDK_INT === Build.VERSION_CODES.KITKAT) {
                    //获取ActivityThread对象

                    //获取ActivityThread对象
                    val sCurrentActivityThread: Any =
                        RefInvoke.getStaticFieldObject(
                            "android.app.ActivityThread",
                            "sCurrentActivityThread"
                        )
                    val mH: Any = RefInvoke.getFieldObject(
                        "android.app.ActivityThread",
                        sCurrentActivityThread,
                        "mH"
                    )
                    RefInvoke.setFieldObject(
                        "android.os.Handler",
                        mH,
                        "mCallback",
                        McallbackMock(mH as Handler)
                    )
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

    }
}

class McallbackMock(private val mH: Handler) : Handler.Callback {
    private val SERVICE_ARGS = 115
    private val STOP_SERVICE = 116
    private val SLEEPING = 137
    val EXECUTE_TRANSACTION = 159//状态模式，统一走这
    override fun handleMessage(msg: Message): Boolean {
        when(msg.what ){
            SERVICE_ARGS -> {
                SpBlockHelper.beforeSPBlock("SERVICE_ARGS")
            }
            STOP_SERVICE -> {
                SpBlockHelper.beforeSPBlock("STOP_SERVICE");
            }
            SLEEPING -> {
                SpBlockHelper.beforeSPBlock("SLEEPING");
            }
            EXECUTE_TRANSACTION -> {

            }
        }
        return false
    }

}

class SpBlockHelper{
    companion object{
        var init = false
        var CLASS_QUEUED_WORK = "android.app.QueuedWork"
        var FIELD_PENDING_FINISHERS = "sPendingWorkFinishers"
        var sPendingWorkFinishers: ConcurrentLinkedQueue<Runnable>? = null
        fun beforeSPBlock(tag: String) {
            if (!init) {
                getPendingWorkFinishers()
                init = true
            }
            if (sPendingWorkFinishers != null) {
                sPendingWorkFinishers!!.clear()
            }
        }

        private fun getPendingWorkFinishers() {
            try {
                val clazz = Class.forName(CLASS_QUEUED_WORK)
                val field: Field = clazz.getDeclaredField(FIELD_PENDING_FINISHERS)
                if (field != null) {
                    field.setAccessible(true)
                    sPendingWorkFinishers =
                        field.get(null) as ConcurrentLinkedQueue<Runnable>?
                }
            } catch (e: Exception) {
            }
        }
    }

}