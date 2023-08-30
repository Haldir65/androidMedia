@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val SUPPORT_NATIVE_BUILD:String  by project
val enableCmake = "true".equals(SUPPORT_NATIVE_BUILD,true)
val SUPPORTED_ABI="arm64-v8a"

android {
    namespace = "com.me.harris.jpegturbo"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        if(enableCmake){
            externalNativeBuild {
                cmake {
                    abiFilters(SUPPORTED_ABI)//只帮我打这个架构的就好了
                    cppFlags("-g -std=c++11 -frtti -fexceptions")
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

dependencies {

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
    implementation(libs.androidx.recyclerview.recyclerview)
}
