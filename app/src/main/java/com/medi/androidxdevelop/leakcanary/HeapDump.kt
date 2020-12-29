package com.medi.androidxdevelop.leakcanary

import android.content.Context
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by lixiang on 2020/12/29
 * Describe:
 */
class HeapDump {
    companion object {
        public fun createDumpFile(context: Context) {
            var sdf = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")
            var createTime = sdf.format(Date(System.currentTimeMillis()))
            var hprofPath = context.externalCacheDir?.absolutePath
            hprofPath = hprofPath.plus("$createTime.hprof")
            try {
                android.os.Debug.dumpHprofData(hprofPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}