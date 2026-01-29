package com.blank.wallpaper

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: ApplicationExtension,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
//
//        composeOptions {
//            kotlinCompilerExtensionVersion = libs.findVersion("androidxComposeCompiler").get().toString()
//        }

        dependencies {
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
        }
    }
}

internal fun Project.configureAndroidComposeLib(
    commonExtension: LibraryExtension,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
//
//        composeOptions {
//            kotlinCompilerExtensionVersion = libs.findVersion("androidxComposeCompiler").get().toString()
//        }

        dependencies {
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
        }
    }
}
