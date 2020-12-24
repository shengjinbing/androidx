package com.medi.androidxdevelop.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.databinding.ActivityDataBindingBinding
import com.medi.androidxdevelop.mvvm.entity.User

class DataBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDataBindingBinding? =
            DataBindingUtil.setContentView<ActivityDataBindingBinding>(
                this,
                R.layout.activity_data_binding
            )
        binding?.user = User("李想", "20")
        val hashMapOf = hashMapOf<String, String>("key" to "haha")
        binding?.map = hashMapOf
    }
}