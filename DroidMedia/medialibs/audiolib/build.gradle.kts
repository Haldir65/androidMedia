@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.me.harris.audiolib"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk =  libs.versions.targetSdkVersion.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(libs.androidx.recyclerview.recyclerview)
}
