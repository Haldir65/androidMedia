@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

//val MIN_SDK_VERSION:String by project
val SUPPORT_NATIVE_BUILD:String  by project
val NDK_VERSION:String  by project
val SUPPORTED_ABI="arm64-v8a"
val enableCmake = "true".equals(SUPPORT_NATIVE_BUILD,true)

val COMPILE_SKD_VERSION:String by project
val BUILD_TOOLS_VERSION:String by project


android {
    namespace = "com.me.harris.pnglib"
    compileSdk = COMPILE_SKD_VERSION.toInt()
    buildToolsVersion = BUILD_TOOLS_VERSION
    ndkVersion = NDK_VERSION

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk =  libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        if(enableCmake){
            externalNativeBuild {
                cmake {
                    version =  "3.30.3"
                    abiFilters(SUPPORTED_ABI)//只帮我打这个架构的就好了
                    cppFlags("-g -std=c++17 -frtti -fexceptions")
                    arguments("-DANDROID_PLATFORM=android-24","-DANDROID_TOOLCHAIN=clang","-DANDROID_CPP_FEATURES=rtti exceptions","-DANDROID_ARM_NEON=true","-DANDROID_STL=c++_shared")
                }
            }
            ndk {
                abiFilters.add(SUPPORTED_ABI)
            }


//        packagingOptions {
//            pickFirst "lib/arm64-v8a/*.so"
//        }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    if (enableCmake){
        externalNativeBuild {
            cmake {
                version =  "3.30.3"
//                path("vendor/libpng-1.6.37/CMakeLists.txt") // build libpng.so
                path("src/main/cpp/CMakeLists.txt") // link libpng.so with mypng.so
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = true
        checkOnly
    }
}


apply(rootProject.projectDir.absolutePath+File.separator+"dependencyHandler.gradle.kts")

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.google.android.material.material)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)

    implementation (projects.awesomelib)
    implementation (projects.serviceloaderapi.serviceapi)


    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core)

//    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
//    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx)

    lifecycle()
    exoplayer()
    coroutine()

    implementation(libs.androidx.cardview.cardview)
    implementation(libs.coil.kt)
    implementation(libs.okio)
    implementation(libs.androidx.recyclerview.recyclerview)

}

fun DependencyHandler.lifecycle(){
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx)
}

fun DependencyHandler.exoplayer(){
    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.ui)
}


fun DependencyHandler.coroutine(){
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
