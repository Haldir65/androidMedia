import org.jetbrains.kotlin.gradle.dsl.JvmTarget

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
//    id("com.android.library")
    alias(libs.plugins.android.library)
//    id("org.jetbrains.kotlin.android")
}
val COMPILE_SKD_VERSION:String by project
val BUILD_TOOLS_VERSION:String by project
val MIN_SDK_VERSION:String by project
val NDK_VERSION:String by project
val CMAKE_VERSION:String by project

android {
    namespace = "com.google.android.renderscript"
    compileSdk = COMPILE_SKD_VERSION.toInt()
    buildToolsVersion = BUILD_TOOLS_VERSION
    ndkVersion = NDK_VERSION
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    testOptions {
        targetSdk = libs.versions.targetSdk.get().toInt() // ✅ Correct way for specifying targetSdk for testing
    }
    lint {
        targetSdk = libs.versions.targetSdk.get().toInt() // ✅ Correct way for specifying targetSdk for linting
    }


    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                version =  CMAKE_VERSION
                cppFlags += "-std=c++17"
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
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

    lint {
        abortOnError = false
        checkReleaseBuilds = true
    }

    externalNativeBuild {
        cmake {
            version =  CMAKE_VERSION
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.google.android.material.material)
//    implementation ("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
}
