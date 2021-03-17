package com.medi.androidxdevelop.utils.sp

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SharedPreferences工具类
 * @param T
 * @property context Context
 * @property name String
 * @property default T
 * @property prefName String
 * @property prefs (android.content.SharedPreferences..android.content.SharedPreferences?)
 * @constructor
 */
class Preference<T>(private val context: Context,
                    private val name: String,
                    private val default: T,
                    private val prefName: String = "default")
    : ReadWriteProperty<Any?, T> {

    private val prefs by lazy {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(findProperName(property))
    }

    private fun findProperName(property: KProperty<*>) = if (name.isEmpty()) property.name else name

    private fun findPreference(key: String): T {
        return when (default) {
            is Long -> prefs.getLong(key, default)
            is Int -> prefs.getInt(key, default)
            is Boolean -> prefs.getBoolean(key, default)
            is String -> prefs.getString(key, default) as Any
            else -> throw IllegalArgumentException("Unsupported type.")
        } as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(findProperName(property), value)
    }

    private fun putPreference(key: String, value: T) {
        with(prefs.edit()) {
            when (value) {
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("Unsupported type.")
            }
        }.apply()
    }

}