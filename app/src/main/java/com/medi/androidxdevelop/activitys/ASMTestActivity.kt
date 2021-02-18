package com.medi.androidxdevelop.activitys

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.createDataStore
import com.hi.dhl.datastore.protobuf.PersonProtos
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.activitys.ui.XLogActivity
import com.medi.androidxdevelop.base.ApplicationContext
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_a_s_m_test.*

class ASMTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_s_m_test)
        initListener()
    }

    /**
     * 注解埋点测试
     * @param view View
     */
    @SensorsDataTrackViewOnClick
    fun testAnnotation(view: View) {
        Toast.makeText(applicationContext, "测试asm", Toast.LENGTH_LONG).show()
    }

    private fun initListener() {
        btn_dialog.setOnClickListener{
            showDialog()
        }
        //测试成功
        btn_cb.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

            }

        })

    }


    /**
     *DialogInterface.OnClickListener测试成功
     */
    private fun showDialog(){
        AlertDialog.Builder(this).apply {
            title = "测试埋点"
            setMessage("我是内容")
            setNegativeButton("取消"
            ) { _, _ -> }
            setPositiveButton("确认",object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }

            })
            create()
            show()
        }
    }

}