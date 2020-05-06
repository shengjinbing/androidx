package com.medi.androidxdevelop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.medi.androidxdevelop.activitys.AppViewScreenActivity
import com.medi.androidxdevelop.mvvm.MvvmActivity
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_mvvm.setOnClickListener {
            startActivity(Intent(this,MvvmActivity::class.java))
        }
        btn_screen.setOnClickListener {
            startActivity(Intent(this,AppViewScreenActivity::class.java))
        }

    }

    @SensorsDataTrackViewOnClick
    fun testAnnotation(view: View) {
        Toast.makeText(applicationContext,"测试asm",Toast.LENGTH_LONG).show()
    }
}
