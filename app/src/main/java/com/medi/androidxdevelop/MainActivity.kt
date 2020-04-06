package com.medi.androidxdevelop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.mvvm.MvvmActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_mvvm.setOnClickListener {
            startActivity(Intent(this,MvvmActivity::class.java))
        }
    }
}
