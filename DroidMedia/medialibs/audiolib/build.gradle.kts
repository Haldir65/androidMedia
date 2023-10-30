@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val SUPPORT_NATIVE_BUILD:String  by project
val NDK_VERSION:String  by project

val enableCmake = "true".equals(SUPPORT_NATIVE_BUILD,true)
val SUPPORTED_ABI="arm64-v8a"

android {
    namespace = "com.me.harris.audiolib"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    ndkVersion = NDK_VERSION


    buildFeatures {
        viewBinding = true
        prefab = true
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk =  libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")


        if(enableCmake){
            externalNativeBuild {
                cmake {
                    abiFilters(SUPPORTED_ABI)//只帮我打这个架构的就好了
                    cppFlags("-g -std=c++17 -frtti -fexceptions")
                    arguments("-DANDROID_PLATFORM=android-26","-DANDROID_TOOLCHAIN=clang","-DANDROID_CPP_FEATURES=rtti exceptions","-DANDROID_ARM_NEON=true","-DANDROID_STL=c++_shared")
                }
            }
            ndk {
                abiFilters.add(SUPPORTED_ABI)
            }
        }
    }

    if (enableCmake){
        externalNativeBuild {
            cmake {
                version =  "3.22.1"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}


apply(from = "${rootProject.rootDir}${File.separator}gradle/scripts/DependencyHandlerExt.gradle.kts")
//apply(from = "${rootProject.rootDir}${File.separator}gradle/scripts/Dependencies.gradle.kts")

fun DependencyHandler.retrofit() {
    implementation(libs.retrofit)
}

dependencies {
    retrofit()
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.google.android.material.material)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)

    implementation (projects.awesomelib)

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
    implementation(libs.oboe)
    implementation(libs.androidx.recyclerview.recyclerview)
}
