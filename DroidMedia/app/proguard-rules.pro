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

### https://github.com/Kotlin/kotlinx.coroutines/issues/1231
# Ensure the custom, fast service loader implementation is removed.

## 一旦开启r8,以下规则默认包含在
## kotlinx-coroutines-android-1.6.4-sources.jar!/META-INF/com.android.tools/r8-from-1.6.0/coroutines.pro
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatcherLoader {
    boolean FAST_SERVICE_LOADER_ENABLED return false;
}
-checkdiscard class kotlinx.coroutines.internal.FastServiceLoader
