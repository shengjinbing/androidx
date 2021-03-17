package com.medi.androidxdevelop.utils.sp
import com.medi.androidxdevelop.base.ApplicationContext

/**
 * sp保存
 */
object CustomDelegateTool {

    fun <T> preference(key: String, default: T, prefName: String = "app") = Preference(
        ApplicationContext.application, key, default, prefName)
}