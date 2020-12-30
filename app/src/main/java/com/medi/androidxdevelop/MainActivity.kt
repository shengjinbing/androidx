package com.medi.androidxdevelop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.medi.androidxdevelop.activitys.*
import com.medi.androidxdevelop.mvvm.MvvmActivity
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_asm.setOnClickListener {
            startActivity(Intent(this, ASMTestActivity::class.java))
        }
        btn_mvvm.setOnClickListener {
            startActivity(Intent(this, MvvmActivity::class.java))
        }
        btn_mvvm.setOnClickListener {
            startActivity(Intent(this, MvvmActivity::class.java))
        }
        coroutine.setOnClickListener {
            startActivity(Intent(this, CoroutinesActivity::class.java))
        }
        btn_screen.setOnClickListener {
            startActivity(Intent(this, AppViewScreenActivity::class.java))
        }

        btn_asynctask.setOnClickListener {
            startActivity(Intent(this, AsyncTaskActivity::class.java))

        }
        btn_leakCannary.setOnClickListener {
            startActivity(Intent(this, LeakCanaryActivity::class.java))
        }
        btn_blockCannary.setOnClickListener {
            startActivity(Intent(this, BlockCanaryActivity::class.java))
        }
    }

}


