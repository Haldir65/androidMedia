import io.gitlab.arturbosch.detekt.Detekt

//import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

//plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    alias(libs.plugins.compose.compiler)
//}

plugins {
    id("wallpaper.android.application")
    id("wallpaper.android.application.compose")
    alias(deps.plugins.compose.compiler)
    id("io.gitlab.arturbosch.detekt").version(deps.versions.detekt)
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
	implementation(deps.accompanist.permissions)

	implementation(deps.exoplayer.core)
	implementation(deps.exoplayer.ui)
    implementation (deps.androidx.activity.ktx)

	implementation(deps.androidx.media3.session)
    implementation(deps.landscapist.glide)
	implementation(deps.androidx.lifecycle.lifecycle.viewmodel.ktx)
	implementation(deps.androidx.lifecycle.lifecycle.viewmodel.compose)
	implementation(deps.androidx.core.core.ktx)
	implementation(platform(deps.androidx.compose.bom))
    implementation(deps.activity.compose)
	implementation(deps.compose.ui)
	implementation(deps.compose.graphics)
	implementation(deps.compose.preview)
	implementation(deps.androidx.material3)
	testImplementation(deps.junit.junit)
	androidTestImplementation(deps.androidx.test.junit)
	androidTestImplementation(deps.espresso.core)
	androidTestImplementation(platform(deps.androidx.compose.bom))
	androidTestImplementation(deps.compose.ui.testing)
	debugImplementation(deps.compose.ui.tooling)
	debugImplementation(deps.compose.ui.test.manifest)
}


detekt {
    // Version of detekt that will be used. When unspecified the latest detekt
    // version found will be used. Override to stay on the same version.
//    toolVersion = "${libs.versions.detekt}"

    // The directories where detekt looks for source files.
    // Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
    source.setFrom("src/main/java", "src/main/kotlin")

    // Builds the AST in parallel. Rules are always executed in parallel.
    // Can lead to speedups in larger projects. `false` by default.
    parallel = false

    // Define the detekt configuration(s) you want to use.
    // Defaults to the default detekt configuration.
    config.setFrom("${rootDir}/config/detekt/detekt.yml")

    // Applies the config files on top of detekt's default config file. `false` by default.
    buildUponDefaultConfig = false

    // Turns on all the rules. `false` by default.
    allRules = false

    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    baseline = file("${rootDir}/config/detekt/baseline.xml")

    // Disables all default detekt rulesets and will only run detekt with custom rules
    // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
    disableDefaultRuleSets = false

    // Adds debug output during task execution. `false` by default.
    debug = false

    // If set to `true` the build does not fail when the
    // maxIssues count was reached. Defaults to `false`.
    ignoreFailures = true

    // Android: Don't create tasks for the specified build types (e.g. "release")
    ignoredBuildTypes = listOf("release")

    // Android: Don't create tasks for the specified build flavor (e.g. "production")
    ignoredFlavors = listOf("production")

    // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
    ignoredVariants = listOf("productionRelease")

    // Specify the base path for file paths in the formatted reports.
    // If not set, all file paths reported will be absolute file path.
    basePath = projectDir.absolutePath
}

tasks {
    val detektAll by registering(Detekt::class) {
        parallel = true
        setSource(files(projectDir))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
        config = files("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = false // prevent task failure
        ignoreFailures = true
    }
}
