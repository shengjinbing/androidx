package com.medi.myplugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by lixiang on 2020/12/17
 * Describe:
 */
open class MyTast : DefaultTask() {

    @TaskAction
    fun action(){
        println("my task run")
    }

}