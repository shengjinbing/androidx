package com.medi.androidxdevelop.mvvm.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Created by lixiang on 2020/12/22
 * Describe:
 */
class UploadWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        uploadImages()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun uploadImages() {
        Log.d("BBBBB","上传图片")
    }
}