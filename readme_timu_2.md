ANR：
1.无论是四大组件或者进程等只要发生ANR，最终都会调用AMS.appNotResponding()方法
2。以下场景都会触发调用AMS.appNotResponding方法:
Service Timeout:比如前台服务在20s内未执行完成；
BroadcastQueue Timeout：比如前台广播在10s内未执行完成
InputDispatching Timeout: 输入事件分发超时5s，包括按键和触摸事件



2.app打包详细流程：链接：https://juejin.cn/post/6844903910734299149
主要有下面几个步骤：

使用 AAPT／AAPT2 编译资源文件生成 resources.arsc 以及 R.java
使用 aidl 处理 aidl 文件，生成 java 文件
使用 JAVAC 编译 java 文件，生成 classes 文件
使用 DX／D8 处理 class 文件，生成最终需要的 dex 文件
使用 Android NDK 处理 native 代码生成 .so 文件
使用 apkbuilder 生成 未签名的 APK
使用 apksigner 对 Apk 进行签名，生成最终的 APK

官方的打包工具在 android_sdk/build-tools/version/ 目录下。

1. AAPT2 compile 资源
使用 AAPT2 处理资源需要两步，compile 和 link，首先执行 compile 操作。执行下面的命令。
/Users/lixiang/Library/Android/sdk/build-tools/29.0.2/aapt2  compile -o tmp/res --dir src/main/res/ 
复制代码使用 aapt2 对单个资源处理，会生成 xxx.flat 文件，是 aapt2 的中间产物，可以用于后面的资源增量编译。我们这里通过 --dir 直接指定了资源的目录，产物 res 是一个压缩包，里面包含了所有资源处理后的 xxx.flat。
这里我们再把 res 这个压缩包解压一下。执行下面的命令。
unzip -u tmp/res -d tmp/aapt2_res
复制代码这一步结束以后，目录是这个样子的。
tmp/
├── aapt2_res
│   └── xxx.flat
├── final
└── res

2. AAPT2 link 资源
AAPT2 link 是把上一步 compile 处理后的 xxx.flat 资源链接，生成一个完整的 resource.arsc，二进制资源和 R.java。执行下面的命令。
/Users/lixiang/Library/Android/sdk/build-tools/29.0.2/aapt2  link -o tmp/res.apk  -I /Users/lixiang/Library/Android/sdk/build-tools/29.0.2/aapt2/android.jar 
--manifest src/main/AndroidManifest.xml  --java tmp 
-R tmp/aapt2_res/drawable-hdpi_ic_launcher_foreground.xml.flat
-R tmp/aapt2_res/mipmap-anydpi-v26_ic_launcher_round.xml.flat
-R tmp/aapt2_res/mipmap-xhdpi_ic_launcher_round.png.flat 
-R tmp/aapt2_res/values_colors.arsc.flat  
.....
--auto-add-overlay
复制代码执行命令后，会生成 res.apk，里面就是 resource.arsc，处理后的 AndroidManifest.xml 以及 处理后的二进制资源。我们这里也把他解压出来，后面最终打包的时候使用。执行命令如下。
unzip -u tmp/res.apk -d tmp/final

3.javac 生成 class 文件
这一步我们需要处理 java 文件，生成 class 文件。要用到上一步生成的 R.java 文件。执行下面的命令。
javac -d tmp src/main/java/com/zy/simpleapk/MainActivity.java  tmp/com/zy/simpleapk/R.java -cp /Users/zy/android-sdk-mac_x86/platforms/android-28/android.jar

4. d8 编译 dex
这一步是把上一步生成的 class 文件编译为 dex 文件，需要用到 d8 或者 dx，这里用 d8。执行下面的命令。
/Users/zy/android-sdk-mac_x86/build-tools/28.0.2/d8 tmp/com/zy/simpleapk/*.class --output tmp --lib /Users/zy/android-sdk-mac_x86/platforms/android-28/android.jar
复制代码命令执行完以后，会生成 classes.dex，这就是最终 apk 里需要的 dex 文件，我们把它拷贝到 final/ 目录下。执行如下命令。
cp tmp/classes.dex tmp/final/classes.dex。

5. 打包 apk
执行完上述的命令，打包 APK 需要的材料就都准备好了，因为 APK 本身就是 zip 格式，这里我们直接用 zip 命令打包上述产物，生成 final.apk。执行下面的命令。
zip -r final.apk *

6. apk 签名
上一步打包好的 APK 还不能直接安装，因为没有签名，我们这里用 debug.keystore 给 final.apk 签名。执行下面的命令。
/Users/zy/android-sdk-mac_x86/build-tools/28.0.2/apksigner sign --ks ~/.android/debug.keystore final.apk
复制代码这里需要输入 debug.keystore 的密码，是 android。
这样，最后的 final.apk 就是我们手动生成的 apk 了。可以安装尝试一下了～


三、多渠道打包
上面介绍了 Android APK 的打包流程，也通过手动打 APK 体验了整个流程。
在实际的生产开发过程中，我们往往会把 APK 发往各个应用市场，很多时候要根据市场渠道进行一些统计，所以就需要对不同的市场渠道进行区分。关于多渠道打包的问题，有不少解决方式。
最容易想到的就是使用 Gradle 的 Flavor，使用 Flavor 的特性，生成不同的渠道标识。不过这种方式每生成一个渠道包都需要执行一遍构建过程，非常耗时。
另外一种方式就是使用 apktool 反编译 APK，修改其中的资源，添加渠道的标识，并进行重新打包签名，这种方式省去了构建过程，反编译，打包，签名这几个步骤也比较耗时。按照美团博客的数据，打包 900 个渠道包将近三个小时。
一个 Zip 文件的格式基本如下：主要可以分为三个大区域：
数据区
核心目录（central directory）
目录结束标识（end of central directory record，EODR）

1.在 APK 签名时，主要流程如下：
计算 APK 中文件摘要，保存摘要的 base64 编码到 MANIFEST.MF 文件中 ->计算 MANIFEST.MF 文件的摘要，保存其 base64 编码到 CERT.SF 文件中 -> 计算 MANIFEST.MF 文件中每个数据块的摘要，
保存其 base64 编码到 CERT.SF 文件中 -> 计算 CERT.SF 文件摘要，通过开发者私钥计算数字签名 -> 保存数字签名和开发者公钥到 CERT.RSA 文件中.
2.在 APK 校验时，主要流程如下：
通过CA 证书解密 CERT.RSA 中的数字证书和对 CERT.SF 的签名 -> 通过签名校验 CERT.SF 文件是否被修改 -> 通过 CERT.SF 验证 MANIFEST.MF 文件是否被修改 -> 通过 CERT.SF 验证 MANIFEST.MF 
文件中数据项是否被修改 -> 通过 MANIFEST.MF 校验 APK 中的文件是否被修改.    

我们在上面讲了 Zip 文件的结构，通过上面校验过程，我们可以发现，在 APK 校验过程中，只是校验了数据区的内容，剩余的两个部分没有做处理。
所以如果修改剩余两个部分，签名校验过程中是不会发现的，要写入信息，EOCD 的注释字段是很好的选择。所以将渠道信息写入 EOCD 的注释字段，就可以达到打入渠道信息的目的。这就是腾讯 VasDolly 做的事情。
除此之外，我们可以发现，APK 签名校验过程中，并没有对 META-INF 文件夹下的文件进行签名和校验，所以可以在 META-INF 文件夹下新增一个空文件，这样也可以携带渠道信息。这就是美团做的事情。



