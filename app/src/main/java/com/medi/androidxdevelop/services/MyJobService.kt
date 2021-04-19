package com.medi.androidxdevelop.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyJobService : JobService(){
    companion object {
        val MYJOBSERVICE_JOB_ID = 0 //作为该jobservice的id标识
        val MYJOBSERVICE_JOB_OVERDIDE_DEADLINE = 1000 //延迟多少秒执行
        private val TAG = "MyJobService"

    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i(TAG, "onStopJob start")
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i(TAG, "onStartJob start")
        return true
    }

}