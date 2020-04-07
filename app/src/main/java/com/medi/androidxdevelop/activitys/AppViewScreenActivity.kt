package com.medi.androidxdevelop.activitys

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.medi.androidxdevelop.MainActivity
import com.medi.androidxdevelop.R
import com.medi.track.screen.SensorsDataAPI

class AppViewScreenActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_view_screen)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ===
            PackageManager.PERMISSION_GRANTED
        ) { //拥有权限

        } else { //没有权限，需要申请全新啊
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        SensorsDataAPI.getInstance().ignoreAutoTrackActivity(MainActivity::class.java)
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) { // 用户点击允许
            } else { // 用户点击禁止
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
        super.onStop()
        SensorsDataAPI.getInstance().removeIgnoredActivity(MainActivity::class.java)
    }

}
