package com.medi.androidxdevelop.base

import android.app.Application
import android.content.Context

/**
 * Description:
 *
 * @author guoyongping
 * @date   2019-12-31 16:09
 */
class ApplicationContext {
    companion object {
        lateinit var application: Application
        lateinit var context: Context
    }
}