package com.medi.androidxdevelop.utils.mmap

import java.io.File
import java.io.RandomAccessFile
import java.lang.reflect.Method
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Created by lixiang on 2021/1/27
 * Describe:
 */
class MmapHelper {

    public fun map(log: String,path:String,position:Long,size:Long) {
        val raf = RandomAccessFile(File(path), "rw")
        //position映射文件的起始位置，size映射文件的大小
        val buffer: MappedByteBuffer = raf.channel.map(FileChannel.MapMode.READ_WRITE, position, size)
        //往缓冲区里写入字节数据
        buffer.put(log.toByte())
    }

    /**
     * 解除内存与文件的映射
     * 有一点比较坑，Java 虽然提供了 map 方法，但是并没有提供 unmap 方法，通过 Google 得知 unmap 方法是有的，
     * 不过是私有的，我们可以通过反射调用获取unmap方法（Android 9.0以上对反射做了限制，可以参考这篇博文绕过限制
     * http://weishu.me/2019/03/16/another-free-reflection-above-android-p/）
     */
    private fun unmap(mbbi: MappedByteBuffer?) {
        if (mbbi == null) {
            return
        }
        try {
            val clazz = Class.forName("sun.nio.ch.FileChannelImpl")
            val m: Method = clazz.getDeclaredMethod("unmap", MappedByteBuffer::class.java)
            m.isAccessible = true
            m.invoke(null, mbbi)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


}