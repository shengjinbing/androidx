 <https://juejin.cn/post/6844903841230487560>
 1.gradle/dsl 自动化构建工具
 2.gradle 项目介绍
    settings.gradle
    build.gradle
    gradle.properties
    gradlew
    dependencies
    flavor
  3.init.gradle
  4.生命周期和hook
    初始化阶段
    配置阶段
    执行阶段
    hook函数
  5.Task
  6.Transform
  7.插件
 
  2.1 settings.gradle
  settings.gradle 是负责配置项目的脚本
  对应 Settings 类，gradle 构建过程中，会根据 settings.gradle 生成 Settings 的对象
 
  2.2 rootproject/build.gradle
  build.gradle 负责整体项目的一些配置，对应的是Project类（重点）
  gradle 构建的时候，会根据 build.gradle 生成Project对象，所以在 build.gradle 里写的 dsl，其实都是 Project
  接口的一些方法，Project 其实是一个接口，真正的实现类是 DefaultProject，build.gradle 里可以调用的方法在 Project 可以查到
  其中几个主要方法有：
  buildscript // 配置脚本的 classpath
  allprojects // 配置项目及其子项目
  respositories // 配置仓库地址，后面的依赖都会去这里配置的地址查找
  dependencies // 配置项目的依赖
 2.3 module/build.gradle
 build.gradle 是子项目的配置，对应的也是 Project 类
 子项目和根项目的配置是差不多的，不过在子项目里可以看到有一个明显的区别，就是引用了一个插件 apply plugin "com.android.application"，后面的 android dsl 就是 application 插件的 extension，关于 android plugin dsl 可以看 android-gradle-dsl
 其中几个主要方法有：
 compileSdkVersion // 指定编译需要的 sdk 版本
 defaultConfig // 指定默认的属性，会运用到所有的 variants 上
 buildTypes // 一些编译属性可以在这里配置，可配置的所有属性在 这里
 productFlavor // 配置项目的 flavor
 
 
 五、gradle 生命周期及回调
 gradle 构建分为三个阶段
 1.初始化阶段
   初始化阶段主要做的事情是有哪些项目需要被构建，然后为对应的项目创建 Project 对象
 2.配置阶段
    配置阶段主要做的事情是对上一步创建的项目进行配置，这时候会执行 build.gradle 脚本，并且会生成要执行的 task
 3.执行阶段
    执行阶段主要做的事情就是执行 task，进行主要的构建工作
 
  Task 的一些重要方法分类如下：
  1.Task 行为
  Task.doFirst
  Task.doLast
 
  2.Task 依赖顺序
  Task.dependsOn
  Task.mustRunAfter
  Task.shouldRunAfter
  Task.finalizedBy
 
  3.Task 的分组描述
  Task.group
  Task.description
 
  4.Task 是否可用
  Task.enabled
 
  5.Task 输入输出
  gradle 会比较 task 的 inputs 和 outputs 来决定 task 是否是最新的，如果 inputs 和 outputs 没有变化，则认为 task 是最新的，task 就会跳过不执行
  Task.inputs
  Task.outputs
 
  6.Task 是否执行
  可以通过指定 Task.upToDateWhen = false 来强制 task 执行Task.upToDateWhen