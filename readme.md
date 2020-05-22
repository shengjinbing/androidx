#walle用法文档：
##用法示例：
生成渠道包 ./gradlew clean assembleReleaseChannels
支持 productFlavors ./gradlew clean assembleMeituanReleaseChannels

##临时生成某渠道包
我们推荐使用channelFile/configFile配置来生成渠道包，但有时也可能有临时生成渠道包需求，这时可以使用：

生成单个渠道包: ./gradlew clean assembleReleaseChannels -PchannelList=meituan

生成多个渠道包: ./gradlew clean assembleReleaseChannels -PchannelList=meituan,dianping

生成渠道包&写入额外信息:

./gradlew clean assembleReleaseChannels -PchannelList=meituan -PextraInfo=buildtime:20161212,hash:xxxxxxx

注意: 这里的extraInfo以key:value形式提供，多个以,分隔。

使用临时channelFile生成渠道包: ./gradlew clean assembleReleaseChannels -PchannelFile=/Users/xx/Documents/channel

使用临时configFile生成渠道包: ./gradlew clean assembleReleaseChannels -PconfigFile=/Users/xx/Documents/config.json

使用上述-P参数后，本次打包channelFile/configFile配置将会失效，其他配置仍然有效。 -PchannelList,-PchannelFile, -PconfigFile三者不可同时使用。

##处理360加固失效问题
1.360加固
2.将未签名的360加固包，使用[ProtectedApkResignerForWalle](https://github.com/Jay-Goo/ProtectedApkResignerForWalle)
3.根目录下运行命令 python ApkResigner.py