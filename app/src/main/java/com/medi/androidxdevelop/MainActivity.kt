package com.medi.androidxdevelop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.medi.androidxdevelop.activitys.*
import com.medi.androidxdevelop.activitys.ui.XLogActivity
import com.medi.androidxdevelop.mvvm.MvvmActivity
import com.tencent.mars.xlog.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("BBBBB","DASDADAS")
        Log.d("BBBBB","DASDADAS1")
        Log.d("BBBBB","DASDADAS2")

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
        btn_kotlin.setOnClickListener {
            startActivity(Intent(this, KotlinActivity::class.java))
        }
        btn_xlog.setOnClickListener {
            startActivity(Intent(this, XLogActivity::class.java))
        }

    }

}


