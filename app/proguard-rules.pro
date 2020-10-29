# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#keep  保留类和类中的成员，防止它们被混淆或移除。
#keepnames 保留类和类中的成员，防止它们被混淆，但当成员没有被引用时会被移除。
#keepclassmembers  只保留类中的成员，防止它们被混淆或移除。
#keepclassmembernames  只保留类中的成员，防止它们被混淆，但当成员没有被引用时会被移除。
#keepclasseswithmembers  保留类和类中的成员，防止它们被混淆或移除，前提是指名的类中的成员必须存在，如果不存在则还是会混淆。
#keepclasseswithmembernames  保留类和类中的成员，防止它们被混淆，但当成员没有被引用时会被移除，前提是指名的类中的成员必须存在，如果不存在则还是会混淆。