package com.medi.hostapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.medi.hostapp.activitys.TestActivity
import com.medi.hostapp.hookhelper.HookHelper
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 插件化的总结：
 * 1.apk的所有资源会设置到LoadedApk中。
 * 2.PackageParser类中，有一个parsePackage方法，接收一个apkFile的参数，即可以是当前apk文件，也可以是外部apk文件。
 *   我们可以使用这个类，来读取插件apk的AndroidManifest文件中的信息，但是PackageParser是隐藏的不对App开发人员开放。
 */
class MainActivity() : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        //startStubActivity()
    }

    /**
     * 这种欺骗手段有个很大的问题，ASM会认为每次打开的都是StubActivity.在AMS端有个栈，会存放每次打开的Activity,那么现在这个栈上面都是
     * StubActivity了，这个相当于那些没用在AndroidManifest中声明的Activity的LaunchMode就只能是默认的类型，即使设置了SingTask和SingleTop，也不会
     * 生效。这个缺陷后续在解决
     */
    fun startStubActivity() {
        HookHelper.hookActivityManagerAndroid29()
        HookHelper.hookActivityThreadmInstrumentation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_resource.setOnClickListener {
            startActivity(Intent(this, ResourceActivity::class.java))
        }

        btn_service.setOnClickListener {
            val intent = Intent()
            intent.setClassName(this, "com.medi.plugin1.TestService")
            startActivity(Intent(intent))
        }
        btn_testactivity.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }


    }
}
