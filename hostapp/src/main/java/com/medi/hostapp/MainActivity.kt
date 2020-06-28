package com.medi.hostapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_resource.setOnClickListener {
            startActivity(Intent(this,ResourceActivity::class.java))
        }

        btn_service.setOnClickListener {
            val intent = Intent()
            intent.setClassName(this,"com.medi.plugin1.TestService")
            startActivity(Intent(intent))
        }


    }
}
