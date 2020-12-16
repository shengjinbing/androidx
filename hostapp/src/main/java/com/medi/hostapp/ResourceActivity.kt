package com.medi.hostapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.medi.hostapp.utils.RefInvoke
import kotlinx.android.synthetic.main.activity_resource.*

class ResourceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)

        plugin1.setOnClickListener {
            val pluginInfo = plugins.get("plugin1.apk")
            loadResources(pluginInfo?.dexPath)
            doSomething(pluginInfo?.classLoader)

        }
    }

    /**
     * 这种方式需要为每个插件创建一个classLoader
     * @param classLoader ClassLoader?
     */
    fun doSomething(classLoader: ClassLoader?) {
        val clazz = classLoader?.loadClass("com.medi.plugin1.UIUtil")
        val str = RefInvoke.invokeStaticMethod(
            clazz,
            "getTextString",
            Context::class.java,
            this
        ) as String
        tv_content.text = str

        val darwable = RefInvoke.invokeStaticMethod(
            clazz,
            "getImageDrawable",
            Context::class.java,
            this
        ) as Drawable

        val drawable = "com.medi.plugin1.R${'$'}drawable"
        val drawableClass = classLoader?.loadClass(drawable)
        val resId = RefInvoke.getStaticFieldObject(drawableClass, "ic_recover") as Int
        iv_icon.setImageResource(resId)

    }
}
