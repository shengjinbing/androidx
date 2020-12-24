package com.medi.androidxdevelop.mvvm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.work.*
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.mvvm.work.ProgressWorker
import com.medi.androidxdevelop.mvvm.work.ProgressWorker.Companion.Progress
import com.medi.androidxdevelop.mvvm.work.SendLogsWorker
import com.medi.androidxdevelop.mvvm.work.UploadWorker
import java.util.concurrent.TimeUnit

class WorkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)
        initWork()
        initLogsWork()
        initLiveData()
        initProgress()

    }

    private fun initProgress() {
        val myWorkRequest = OneTimeWorkRequest.from(ProgressWorker::class.java)
        WorkManager.getInstance(applicationContext).enqueue(myWorkRequest)
        WorkManager.getInstance(applicationContext)
            // requestId is the WorkRequest id
            .getWorkInfoByIdLiveData(myWorkRequest.id)
            .observe(this, Observer { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    val progress = workInfo.progress
                    val value = progress.getInt(Progress, 0)
                    // Do something with progress information
                    Log.d("BBBBB", "progress==$progress  value==$value")
                }
            })
    }

    private fun initLiveData() {
        val workInfosForUniqueWork =
            WorkManager.getInstance(this).getWorkInfosForUniqueWork("sendLogs")
        //取消工作
        WorkManager.getInstance(this).cancelUniqueWork("sendLogs")
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(sendLogsWorkRequest.id)
            .observe(this) { workInfo ->
                if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                    Toast.makeText(applicationContext, "sasasa", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * 工作约束:约束可确保将工作延迟到满足最佳条件时运行。以下约束适用于 WorkManager。
     *
     * NetworkType	约束运行工作所需的网络类型。例如 Wi-Fi (UNMETERED)。
     * BatteryNotLow	如果设置为 true，那么当设备处于“电量不足模式”时，工作不会运行。
     * RequiresCharging	如果设置为 true，那么工作只能在设备充电时运行。
     * DeviceIdle	如果设置为 true，则要求用户的设备必须处于空闲状态，才能运行工作。如果您要运行批量操作，否则可能会
     *             降低用户设备上正在积极运行的其他应用的性能，建议您使用此约束。
     * StorageNotLow	如果设置为 true，那么当用户设备上的存储空间不足时，工作不会运行。
     *
     * 如需创建一组约束并将其与某项工作相关联，请使用一个 Contraints.Builder() 创建 Constraints 实例，并将该实例
     * 分配给 WorkRequest.Builder()。
     */
    private lateinit var sendLogsWorkRequest: PeriodicWorkRequest
    private fun initLogsWork() {
        sendLogsWorkRequest =
            PeriodicWorkRequestBuilder<SendLogsWorker>(10, TimeUnit.SECONDS)
                /* .setConstraints(Constraints.Builder()
                     .setRequiresCharging(true)
                     .build()
                 )*/
                .build()
        /**
         *  WorkManager.enqueueUniqueWork()（用于一次性工作）
         * WorkManager.enqueueUniquePeriodicWork()（用于定期工作）
         * uniqueWorkName - 用于唯一标识工作请求的 String。
         * existingWorkPolicy - 此 enum 可告知 WorkManager 如果已有使用该名称且尚未完成的唯一工作链，
         *     应执行什么操作。如需了解详情，请参阅冲突解决政策。
         * work - 要调度的 WorkRequest。
         */
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sendLogs",
            ExistingPeriodicWorkPolicy.KEEP,
            sendLogsWorkRequest
        )

    }

    private fun initWork() {
        //一次性工作
        val myWorkRequest = OneTimeWorkRequest.from(UploadWorker::class.java)
        //对于更复杂的工作，可以使用构建器。
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>()
                .build()

        //调度定期工作,工作的运行时间间隔定为一小时。
        val saveRequest =
            PeriodicWorkRequestBuilder<UploadWorker>(1, TimeUnit.HOURS)
                // Additional configuration
                .build()

        WorkManager
            .getInstance(this)
            .enqueue(uploadWorkRequest)
    }
}