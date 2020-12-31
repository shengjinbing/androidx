package com.sensorsdata.analytics.javassist.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by lixiang on 2020/12/30
 * Describe:
 */
class SensorsAnalyticsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        var appExtension = project.extensions.findByType(AppExtension::class.java)
        appExtension?.registerTransform(SensorsAnalyticsTransform(project))
    }

}