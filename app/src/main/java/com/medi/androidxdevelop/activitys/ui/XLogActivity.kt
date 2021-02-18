package com.medi.androidxdevelop.activitys.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.hi.dhl.datastore.protobuf.PersonProtos
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.base.ApplicationContext.Companion.context
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream


/**
 * https://juejin.cn/post/6850418121279438855
 * 1.cacheDir 设置缓存目录
 *缓存目录，当 logDir 不可写时候会写进这个目录，可选项，不选用请给 ""， 如若要给，建议给应用的 /data/data/packname/files/log 目录。
 *会在目录下生成后缀为 .mmap3 的缓存文件，
 *2.logDir 设置写入的文件目录
 *真正的日志，后缀为 .xlog。日志写入目录，请给单独的目录，除了日志文件不要把其他文件放入该目录，不然可能会被日志的自动清理功能清理掉。
 *3.save private key
 * e0c23aee232bd8371a26da5148d78531dc7913ad875f7de4783ba9c0ce0a158c
 * appender_open's parameter:
 * 4d5529e4c5d2ae103cc713e73ab9ab7c91557e1c245d9407c40601f4ee7da22669dfd0b1186dc7005c836635ca2d2c9cd38b9e5bd0bc568410a5b98f614f97ed
 *
 *
 * Jetpack DataStore 是一种数据存储解决方案
 * Preferences DataStore 使用键存储和访问数据。此实现不需要预定义的架构，也不确保类型安全。
 * Proto DataStore 将数据作为自定义数据类型的实例进行存储。此实现要求您使用协议缓冲区来定义架构，但可以确保类型安全。
 * 1.Preferences DataStore 实现使用 DataStore 和 Preferences 类将简单的键值对保留在磁盘上。
 * 2.Proto DataStore 实现使用 DataStore 和协议缓冲区将类型化的对象保留在磁盘上。
 */
class XLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_x_log)
        initData()
        initPreferencesDataStore()
        initProtoDataStore()
        migrateSP()
    }
    //迁移 SharedPreferences 到 DataStore
    private fun migrateSP(){
        var dataStore = context.createDataStore(
            name = "PREFERENCE_NAME",
            migrations = listOf(
                SharedPreferencesMigration(
                    context,
                    "SharedPreferencesRepository.PREFERENCE_NAME"
                )
            )
        )
    }

    //https://mp.weixin.qq.com/s/lvl2LBJP2yuQ4OpzzDA30w 再见 SharedPreferences ，Jetpack DataStore 第二种实现方式
    private fun initProtoDataStore(){

    }

   /* val protoDataStore: DataStore<PersonProtos.Person> = context.createDataStore(
        fileName = "settings.pb",
        serializer = PersonSerializer
    )

    fun readData(): Flow<PersonProtos.Person> {
        return protoDataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(PersonProtos.Person.getDefaultInstance())
                } else {
                    throw it
                }
            }
    }
    suspend fun saveData(personModel: PersonProtos.Person) {
        protoDataStore.updateData { person ->
            person.toBuilder().setAge(personModel.age).setName(personModel.name).build()
        }
    }*/

    object PersonSerializer : Serializer<PersonProtos.Person> {
        override val defaultValue: PersonProtos.Person = PersonProtos.Person.getDefaultInstance()

        //读消息
        override fun readFrom(input: InputStream): PersonProtos.Person {
            try {
                return PersonProtos.Person.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }
        //写消息
        override fun writeTo(
            t: PersonProtos.Person,
            output: OutputStream
        ) = t.writeTo(output)
    }


    lateinit var EXAMPLE_COUNTER:Preferences.Key<Int>
    private fun initPreferencesDataStore(){
        val dataStore = createDataStore(
           "settings"
        )
        //取值
        EXAMPLE_COUNTER = intPreferencesKey("EXAMPLE_COUNTER")
        dataStore.data.map {
            it[EXAMPLE_COUNTER]?:0
        }

    }
    //设置值
    suspend fun incrementCounter(dataStore:DataStore<Preferences>) {
        dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }

    /**
     * MMKV 原理https://github.com/Tencent/MMKV/wiki/design
     * 内存准备
     * 通过 mmap 内存映射文件，提供一段可供随时写入的内存块，App 只管往里面写数据，由操作系统负责将内存回写到文件，不必担心 crash 导致数据丢失。
     * 数据组织
     * 数据序列化方面我们选用 protobuf 协议，pb 在性能和空间占用上都有不错的表现。
     * 写入优化
     * 考虑到主要使用场景是频繁地进行写入更新，我们需要有增量更新的能力。我们考虑将增量 kv 对象序列化后，append 到内存末尾。
     * 空间增长
     * 使用 append 实现增量更新带来了一个新的问题，就是不断 append 的话，文件大小会增长得不可控。我们需要在性能和空间上做个折中。
     */
    private fun initData() {
        val kv = MMKV.defaultMMKV()!!
        kv.encode("bool", true)
        val bValue = kv.decodeBool("bool")
        kv.encode("int", Int.MIN_VALUE)
        val iValue = kv.decodeInt("int")
        kv.encode("string", "Hello from mmkv")
        val str = kv.decodeString("string")
    }

    /**
     * 剖析 SharedPreferences apply 引起的 ANR 问题https://blog.csdn.net/Fantasy_Lin_/article/details/109710020
     * 1.温和改良派
     * 低频 尽量保证多次edit一个apply,原因上文讲过，尽量维持低频的写入。
     * 异步 能用apply()方法提交的就用apply()方法提交，原因这个方法是异步的，有延迟的（100s）
     * 小量 尽量维持Sharepreferences的体量小些，方便磁盘快速写入。
     * 合规 如果存JSON数据，就不要使用Sharepreferences了，因为SharedPerences本质是xml文件格式存储的，要存储JSON文件需要转义效率很低。不如直接自己编写代码文件读写在App私有目录中存储。
     * 2.ANR问题的罪魁祸首
     *  waitToFinish()会将，储存在QueuedWork的操作一并处理掉。什么时候呢？在Activiy的 onStop()、BroadcastReceiver的onReceive()
     * 以及Service的onStartCommand()，onStop（）方法之前都会调用waitToFinish()。大家知道这些方法都是执行在主线程中，一旦waitToFinish()执行超时，就会跑出ANR。
     * 解决办法：清空等待队列SpHook
     * 3.文件损坏 & 备份机制https://mp.weixin.qq.com/s/cyouNcCkC0yCMGK0doLENQ
     * SharedPreferences的写入操作正式执行之前，首先会对文件进行备份，将初始文件重命名为增加了一个.bak后缀的备份文件：这之后，尝试对文件进行写入操作，写入成功时，则将备份文件删除：
     * 反之，若因异常情况（比如进程被杀）导致写入失败，进程再次启动后，若发现存在备份文件，则将备份文件重名为源文件，原本未完成写入的文件就直接丢弃：
     * 4.如何保证进程安全
     * 实现思路很多，比如使用文件锁，保证每次只有一个进程在访问这个文件；或者对于Android开发而言，ContentProvider作为官方
     * 倡导的跨进程组件，其它进程通过定制的ContentProvider用于访问SharedPreferences，同样可以保证SharedPreferences的进程安全；等等。
     * 5.SharedPreference使用的问题：
     *   1.通过 getXXX() 方法获取数据，可能会导致主线程阻塞
     *   2.SharedPreference 不能保证类型安全
     *   3.SharedPreference 加载的数据会一直留在内存中，浪费内存
     *   4.apply() 方法虽然是异步的，可能会发生 ANR，在 8.0 之前和 8.0 之后实现各不相同
     *   5.apply() 方法无法获取到操作成功或者失败的结果
     *   6.在 8.0 之前，调用 N 次 apply() 方法，就会执行 N 次磁盘写入，在 8.0 之后，apply() 方法调用了多次，只会执行最后一次写入，通过版本号来控制的。
     */
    private fun initSp(){
        val sp = getSharedPreferences("test", Context.MODE_PRIVATE)//异步加载 SP 文件内容
        /**
         * 1.等待 SP 加载完毕，在同步方法内调用了 wait() 方法，会一直等待 getSharedPreferences()方法开启的线程读取完数据
         * 才能继续往下执行，如果读取几 KB 的数据还好，假设读取一个大的文件，势必会造成主线程阻塞。
         * 2.SP 不能保证类型安全,调用 getXXX() 方法的时候，可能会出现 ClassCastException 异常，因为使用相同的 key 进行操作的时候，
         *   putXXX 方法可以使用不同类型的数据覆盖掉相同的 key。
         */
        sp.getBoolean("aa",false)

        val edit = sp.edit()
        edit.commit()
        edit.apply()
    }

}