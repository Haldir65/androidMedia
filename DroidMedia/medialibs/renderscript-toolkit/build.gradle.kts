@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
val COMPILE_SKD_VERSION:String by project
val MIN_SDK_VERSION:String by project
val NDK_VERSION:String by project

android {
    namespace = "com.google.android.renderscript"
    compileSdk = COMPILE_SKD_VERSION.toInt()
    buildToolsVersion = "35.0.0"
    ndkVersion = NDK_VERSION
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }



    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk =  libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                version =  "3.30.3"
                cppFlags += "-std=c++17"
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

    lint {
        abortOnError = false
        checkReleaseBuilds = true
    }

    externalNativeBuild {
        cmake {
            version =  "3.30.3"
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.google.android.material.material)
//    implementation ("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
}
