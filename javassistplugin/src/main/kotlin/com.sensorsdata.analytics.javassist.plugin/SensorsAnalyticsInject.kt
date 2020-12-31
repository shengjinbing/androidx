package com.sensorsdata.analytics.javassist.plugin

import javassist.ClassPool
import javassist.bytecode.AnnotationsAttribute
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.regex.Matcher
import java.util.zip.ZipEntry

/**
 * Created by lixiang on 2020/12/30
 * Describe:
 */
class SensorsAnalyticsInject {
    companion object {
        //ClassPool是CtClass对象的容器，可以按需读取文件用来创建并保存，CtClass对象，以便之后可能会被使用到
        private val pool: ClassPool = ClassPool.getDefault()//使用的是默认系统的类搜索路径（JVM的类搜索路径）
        private const val SDK_HELPER: String =
            "com.sensorsdata.analytics.android.sdk.SensorsDataAutoTrackHelper"

        private fun appendClassPath(libPath: String) {
            pool.appendClassPath(libPath)
        }

        /**
         * 这里需要将jar包先解压，注入代码后再重新生成jar包
         * @path jar包的绝对路径
         */
        fun injectJar(path: String, project: Project): File? {
            appendClassPath(path)
            if (path.endsWith(".jar")) {
                //pool.appendClassPath(project.android.bootClasspath[0].toString())
                val jarFile = File(path)
                // jar包解压后的保存路径
                val jarZipDir = jarFile.parent + File.separator + jarFile.name.replace(".jar", "")

                // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
                val classNameList: List<File> = unzipJar(path, jarZipDir)

                // 删除原来的jar包
                jarFile.delete()

                // 注入代码
                pool.appendClassPath(jarZipDir)
                classNameList.forEach {
                    injectClass(it, jarZipDir)
                }
                // 重新打包jar
                zipJar(jarZipDir, path)

                // 删除目录
                FileUtils.deleteDirectory(File(jarZipDir))

                return jarFile
            }

            return null
        }

        private fun injectClass(classFile: File, path: String) {
            val filePath = classFile.absolutePath
            if (!filePath.endsWith(".class")) {
                return
            }

            if (!filePath.contains("R$")
                && !filePath.contains("R2$")
                && !filePath.contains("R.class")
                && !filePath.contains("R2.class")
                && !filePath.contains("BuildConfig.class")
            ) {
                val className = filePath.substring(path.length + 1, filePath.length - 6).replace(
                    Matcher.quoteReplacement(File.separator), "."
                )
                if (!className.startsWith("android")) {
                    try {
                        //1.第一步获取class对象，即：CtClass;一个CtClass对象可以处理一个.class文件
                        val ctClass = pool.getCtClass(className)
                        //解冻
                        if (ctClass.isFrozen) {
                            ctClass.defrost()
                        }
                        var modified = false
                        //2.获取获取class实现的接口列表
                        val interfaces = ctClass.interfaces
                        if (interfaces != null) {
                            val interfaceList = HashSet<String>()
                            for (c1 in interfaces) {
                                interfaceList.add(c1.name)
                            }
                            //3.获取class方法的所有方法列表
                            for (currentMethod in ctClass.declaredMethods) {
                                val methodInfo = currentMethod.methodInfo
                                val attribute = methodInfo
                                    .getAttribute(AnnotationsAttribute.visibleTag) as AnnotationsAttribute
                                for (annotation in attribute.annotations) {
                                    if ("@com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick" == annotation.toString()) {
                                        if ("(Landroid/view/View;)V" == currentMethod.signature) {
                                            /**
                                             * 在方法体体重插入代码，
                                             * insertBefore、
                                             * insertAfter、
                                             * addCatch、插入方法体抛出异常时执行的代码
                                             * insertAt
                                             * $0,等价于this指针。如果方法是静态的不可用
                                             * $1,第一个参数
                                             * $2,第二个参数
                                             */
                                            currentMethod.insertAfter("$SDK_HELPER.trackViewOnClick(\$1);")
                                            modified = true
                                            break
                                        }
                                    }
                                }

                                val methodSignature = currentMethod.name + currentMethod.signature

                                if ("onContextItemSelected(Landroid/view/MenuItem;)Z" == methodSignature) {
                                    currentMethod.insertAfter("$SDK_HELPER.trackViewOnClick(\$0,\$1);")
                                    modified = true
                                } else if ("onOptionsItemSelected(Landroid/view/MenuItem;)Z" == methodSignature) {
                                    currentMethod.insertAfter("$SDK_HELPER.trackViewOnClick(\$0,\$1);")
                                    modified = true
                                } else {
                                    val methodCell = SensorsAnalyticsConfig.isMatched(
                                        interfaceList,
                                        methodSignature
                                    )
                                    if (methodCell != null) {
                                        val stringBuffer = StringBuffer()
                                        stringBuffer.append(SDK_HELPER)
                                        stringBuffer.append(".trackViewOnClick(")
                                        //不包含尾元素
                                        for (i in methodCell.paramStart until methodCell.paramStart + methodCell.paramCount) {
                                            stringBuffer.append("\$")
                                            stringBuffer.append(i)
                                            if (i != (methodCell.paramStart + methodCell.paramCount - 1)) {
                                                stringBuffer.append(",")
                                            }
                                        }
                                        stringBuffer.append(");")
                                        currentMethod.insertAfter(stringBuffer.toString())
                                        modified = true
                                    }
                                }
                            }
                        }

                        if (modified) {
                            /**
                             * 冻结类：
                             * 1.如果一个CtClass对象通过writeFile，toClass，toBytecode等方法被转换成一个类文件，此CtClass对象就会被冻结起来，不允许被修改，这是因为一个类只能被JVM加载一次
                             * 2.也可以通过defrost方法解冻
                             */
                            ctClass.writeFile(path)//会将CtClass对象转成类文件并写到本地磁盘。
                            ctClass.detach()//从ClassPool中删除，避免内存溢出
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun injectDir(path: String, project: Project) {
            try {
                pool.appendClassPath(path)
                /**加入android.jar，不然找不到android相关的所有类*/
                //pool.appendClassPath(project.android.bootClasspath[0].toString())

                val dir = File(path)
                if (dir.isDirectory) {
                    dir.listFiles()?.forEach {
                        injectClass(it, path)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun unzipJar(jarPath: String, destDirPath: String): List<File> {
            val fileList = ArrayList<File>()
            if (jarPath.endsWith(".jar")) {
                val jarFile = JarFile(jarPath)
                val jarEntries = jarFile.entries()
                while (jarEntries.hasMoreElements()) {
                    val jarEntry = jarEntries.nextElement()
                    if (jarEntry.isDirectory) {
                        continue
                    }
                    val entryName = jarEntry.name
                    val outFileName = destDirPath + File.separator + entryName
                    val outFile = File(outFileName)
                    fileList.add(outFile)
                    outFile.parentFile.mkdirs()
                    val inputStream = jarFile.getInputStream(jarEntry)
                    val fileOutputStream = FileOutputStream(outFile)
                    val buffer = ByteArray(1024)
                    while (inputStream.read(buffer) != -1) {
                        fileOutputStream.write(buffer)
                    }
                    fileOutputStream.close()
                    inputStream.close()
                }
                jarFile.close()
            }

            return fileList
        }

        private fun zipJar(packagePath: String, destPath: String) {
            val file = File(packagePath)
            val outputStream = JarOutputStream(FileOutputStream(destPath))
            file.listFiles()?.forEach { f ->
                val entryName = f.absolutePath.substring(file.absolutePath.length + 1)
                outputStream.putNextEntry(ZipEntry(entryName))
                if (!f.isDirectory) {
                    val inputStream = FileInputStream(f)
                    val buffer = ByteArray(1024)
                    while (inputStream.read(buffer) != -1) {
                        outputStream.write(buffer)
                    }
                    inputStream.close()
                }
            }
            outputStream.close()
        }
    }
}