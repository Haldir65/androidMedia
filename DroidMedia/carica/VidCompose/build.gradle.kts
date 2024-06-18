//import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

//plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    alias(libs.plugins.compose.compiler)
//}

plugins {
    id("wallpaper.android.application")
    id("wallpaper.android.application.compose")
    alias(libs.plugins.compose.compiler)
//    id("wallpaper.android.hilt")
}

//
//val COMPILE_SKD_VERSION:String by project
////val MIN_SDK_VERSION:String by project
//val SUPPORT_NATIVE_BUILD:String  by project
//val NDK_VERSION:String  by project

android {
	namespace = "com.codingwithumair.app.vidcompose"
   /* compileSdk = COMPILE_SKD_VERSION.toInt()

	defaultConfig {
//		applicationId = "com.codingwithumair.app.vidcompose"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk =  libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}*/

//	buildTypes {
//		release {
//			isMinifyEnabled = false
//			proguardFiles(
//				getDefaultProguardFile("proguard-android-optimize.txt"),
//				"proguard-rules.pro"
//			)
//		}
//	}
//	compileOptions {
//		sourceCompatibility = JavaVersion.VERSION_21
//		targetCompatibility = JavaVersion.VERSION_21
//	}
//	kotlinOptions {
//		jvmTarget = "21"
//	}
//	buildFeatures {
//		viewBinding = true
//		compose = true
//	}
//	composeOptions {
//		kotlinCompilerExtensionVersion = "1.5.14"
//	}
//	packaging {
//		resources {
//			excludes += "/META-INF/{AL2.0,LGPL2.1}"
//		}
//	}
}

dependencies {
	implementation(libs.accompanist.permissions)

	implementation(libs.exoplayer.core)
	implementation(libs.exoplayer.ui)
	implementation(libs.androidx.media3.session)
    implementation(libs.landscapist.glide)
	implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.lifecycle.lifecycle.viewmodel.compose)
	implementation(libs.core.ktx)
	implementation(platform(libs.compose.bom))
    implementation("androidx.activity:activity-compose")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	testImplementation(libs.junit)
	androidTestImplementation(libs.junit.ext)
	androidTestImplementation(libs.espresso.core)
	androidTestImplementation(platform(libs.compose.bom))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
}

