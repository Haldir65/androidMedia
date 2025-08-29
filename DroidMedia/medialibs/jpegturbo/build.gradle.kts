@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val SUPPORT_NATIVE_BUILD:String  by project
val enableCmake = "true".equals(SUPPORT_NATIVE_BUILD,true)
val SUPPORTED_ABI="arm64-v8a"
val MIN_SDK_VERSION:String by project
val NDK_VERSION:String  by project
val CMAKE_VERSION:String  by project



val COMPILE_SKD_VERSION:String by project
val BUILD_TOOLS_VERSION:String by project

android {
    namespace = "com.me.harris.jpegturbo"
    compileSdk = COMPILE_SKD_VERSION.toInt()
    buildToolsVersion = BUILD_TOOLS_VERSION
    buildFeatures {
        viewBinding = true
    }
    ndkVersion = NDK_VERSION

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk =  libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        if(enableCmake){
            externalNativeBuild {
                cmake {
                    version =  CMAKE_VERSION
                    abiFilters(SUPPORTED_ABI)//只帮我打这个架构的就好了
                    cppFlags("-g -std=c++17 -frtti -fexceptions")
                    arguments("-DANDROID_PLATFORM=android-24","-DANDROID_TOOLCHAIN=clang","-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON","-DANDROID_CPP_FEATURES=rtti exceptions","-DANDROID_ARM_NEON=true","-DANDROID_STL=c++_shared")
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

    if (enableCmake){
        externalNativeBuild {
            cmake {
                version =  CMAKE_VERSION
                path("src/main/cpp/CMakeLists.txt")
            }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.google.android.material.material)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)

    implementation (projects.awesomelib)
    implementation (projects.serviceloaderapi.serviceapi)
    implementation (projects.medialibs.renderscriptToolkit)


    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core)

    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx)
    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.ui)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.cardview.cardview)
    implementation(libs.coil.kt)
    implementation(libs.okio)
    implementation(libs.androidx.recyclerview.recyclerview)

}
