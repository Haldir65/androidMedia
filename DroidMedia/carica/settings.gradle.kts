pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// https://github.com/Kotlin/kmm-basic-sample/blob/master/settings.gradle.kts

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
    versionCatalogs {
        create("deps") {
            from(files(rootDir.parentFile.resolve("gradle/libs.versions.toml")))
        }
    }
}

rootProject.name = "wallpaper"
include(":VidCompose")
//include(":data")
//include(":domain")
//include(":core")
//include(":designsystem")
//include(":feature:home")
//include(":feature:about")
//include(":feature:collection")
//include(":ui")
