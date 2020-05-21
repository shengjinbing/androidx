package com.medi.plugin1

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class TestService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext,"打开了插件中的服务",Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
