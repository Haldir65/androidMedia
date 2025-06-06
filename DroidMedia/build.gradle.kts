import io.gitlab.arturbosch.detekt.Detekt

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

//    ext.kotlin_version = '1.9.22'
//    ext.hilt_version = "2.49"
    /// see https://github.com/android/nowinandroid/blob/main/build.gradle.kts
//    val slurper = groovy.json.JsonSlurper()
//    val ABI = property('SUPPORTED_ABI').toString()
//    val SUPPORT_NATIVE_BUILD = "True".equalsIgnoreCase(property('SUPPORT_NATIVE_BUILD'))
//    val SUPPORTED_ABI = ['arm64-v8a', 'x86', 'x86_64', 'armeabi-v7a'] as Array<String>
//    project.logger.lifecycle("supported abi = ${SUPPORTED_ABI} ${SUPPORTED_ABI.size()}")
//    ext.SUPPORTED_ABI = SUPPORTED_ABI
//    ext.SUPPORT_NATIVE_BUILD = SUPPORT_NATIVE_BUILD

    repositories {
        // In Android, all plugins are found in the google() and mavenCentral() repositories.
        // However, your build might need third-party plugins that are resolved using the gradlePluginPortal() service.
        google()
        mavenCentral()
        gradlePluginPortal()
        // Therefore, experiment with the gradlePluginPortal() entry
        // by putting it last in the repository block in your settings.gradle file.
    }
    dependencies {

//        id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
//        classpath(libs.android.gradlePlugin)
//        def libs = project.extensions.getByType(VersionCatalogsExtension).named("libs") as org.gradle.accessors.dm.LibrariesForLibs
//        classpath(libs.android.gradle.build.plugin)
        //https://github.com/gradle/gradle/issues/16958
        // version catalog for should work for buildScripts

//        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    // Existing plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.detekt)
}



val detektFormatting = libs.detekt.formatting

//subprojects {
//    apply {
//        plugin("io.gitlab.arturbosch.detekt")
//    }
//
//    detekt {
//        config.from(rootProject.files("config/detekt/detekt.yml"))
//        ignoreFailures = true
//    }
//
//    dependencies {
//        detektPlugins(detektFormatting)
//    }
//}

tasks {
    val detektAll by registering(Detekt::class) {
        parallel = true
        setSource(files(projectDir))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
        config = files("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = false
        ignoreFailures = true
    }
}


//allprojects {
//    repositories {
//        google()
//        mavenCentral()
//    }
//}

//tasks.register('clean', Delete) {
//    delete rootProject . buildDir
//}
//
//task checkJetifierAll (group: "verification") { }
//
//subprojects { project ->
//    project.tasks.configureEach { task ->
//        if (task.name == "checkJetifier") {
//            checkJetifierAll.dependsOn(task)
//        }
//    }
//}

//作者：程序员江同学
//链接：https://juejin.cn/post/7153250843905654798
//来源：稀土掘金
//著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
