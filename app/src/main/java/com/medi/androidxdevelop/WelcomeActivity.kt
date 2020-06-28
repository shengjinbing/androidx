package com.medi.androidxdevelop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*
import org.jetbrains.anko.sdk25.listeners.onClick

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        btn_time.onClick { startActivity(Intent(this,MainActivity::class.java)) }
    }
}
