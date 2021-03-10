package com.sensorsdata.analytics.android.plugin

import com.android.build.gradle.AppExtension
import com.sensorsdata.analytics.android.plugin.extension.SensorsAnalyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ASM框架核心的相关类
 * 1.ClassReader
 *   该类主要用来解析编译过的.class文件
 * 2.ClassWriter
 *   该类主要是用来重新构建编译后的类，比如修改类名、属性以及方法、甚至可以生成新的类字节码文件
 * 3.ClassVisitor
 *   主要负责"拜访类的成员信息"，其中包含标记在类上的注解、类的构造方法、类的字段、类的方法、静态代码
 * 4.AdviceAdapter
 *   实现了MethodVisitor接口、主要负责"拜访方法的信息",用来具体的方法字节码操作。
 *
 * ASM所有对View插庄的总结
 * 1.View针对实现View.OnClickListener接口的，onclick（View view）
 *  完善：android：onclick属性绑定，通过方法增加注解来实现。
 * 2.AlertDialog DialogInterface.OnClickListener接口，两个按钮取消和确认。
 *   v7包下面的AlertDialog  DialogInterface.OnMultiChoiceClickListener
 * 3.MenuItem
 * 4.CheckBox\SwitchCompat\RadioButton\ToggleButton\RadioGroup的点击事件
 * 5.支持采集RatingBar
 * 6.支持采集SeekBar
 * 7.支持采集Spinner
 * 8.采集TabHost的点击事件
 * 9.支持采集ListView\GridView\RecyclerView
 *
 * AspectJ AOP面向切面编程，通过AOP可以再编译期间对代码进行动态管理，以达到统一维护的目的。
 * 优点：
 * 1、无侵入性
 * 2、修改方便
 * 缺点：
 *   1.无法组织第三方库
 *   2.由于定义的切点依赖编程语言，目前该方案无法兼容Lambda语法
 *   3.会有一个兼容性问题，比如：D8、Gradle4.x
 *
 * javasisit 是一个处理java字节码的类库，他可以在编译好的类中添加新的方法，或者修改已有方法，并不需要对字节码方面有深入了解
 *    可以绕过编译直接操作字节码从而实现代码注入。
 * 1.ClassPool是CtClass的容器，一旦CtClass被创建就被保存进去。
 */
class SensorsAnalyticsPlugin implements Plugin<Project> {
    void apply(Project project) {
        SensorsAnalyticsExtension extension = project.extensions.create("sensorsAnalytics", SensorsAnalyticsExtension)

        //关闭埋点插件
        boolean disableSensorsAnalyticsPlugin = false
        Properties properties = new Properties()
        if (project.rootProject.file('gradle.properties').exists()) {
            properties.load(project.rootProject.file('gradle.properties').newDataInputStream())
            disableSensorsAnalyticsPlugin = Boolean.parseBoolean(properties.getProperty("sensorsAnalytics.disablePlugin", "false"))
        }

        if (!disableSensorsAnalyticsPlugin) {
            AppExtension appExtension = project.extensions.findByType(AppExtension.class)
            appExtension.registerTransform(new SensorsAnalyticsTransform(project, extension))
        } else {
            println("------------您已关闭了神策插件--------------")
        }
    }
}