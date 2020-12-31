package com.sensorsdata.analytics.javassist.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by lixiang on 2020/12/30
 * Describe:
 */
class SensorsAnalyticsTransform(private val project: Project) : Transform() {
    override fun getName(): String {
        return "SensorsAnalyticsAutoTrack_Javassist"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        transformInvocation?.run {
            if (!isIncremental){
                outputProvider.deleteAll()
            }
            inputs.forEach { input ->
                input.directoryInputs.forEach {
                    SensorsAnalyticsInject.injectDir(it.file.absolutePath, project)
                    var dest = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                    /**将input的目录复制到output指定目录*/
                    FileUtils.copyDirectory(it.file, dest)
                }

                input.jarInputs.forEach {
                    /**重命名输出文件（同目录copyFile会冲突）*/
                    var destName = it.file.name
                    /**截取文件路径的 md5 值重命名输出文件,因为可能同名,会覆盖*/
                    var hexName = DigestUtils.md5Hex(it.file.absolutePath).substring(0, 8)
                    /** 获取 jar 名字*/
                    if (destName.endsWith(".jar")) {
                        destName = destName.substring(0, destName.length - 4)
                    }
                    var copyJarFile = SensorsAnalyticsInject.injectJar(it.file.absolutePath, project)
                    var dest = outputProvider.getContentLocation(destName + hexName, it.contentTypes, it.scopes, Format.JAR)
                    FileUtils.copyFile(copyJarFile, dest)

                    context.temporaryDir.delete()
                }

            }
        }


    }

}