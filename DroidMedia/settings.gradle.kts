pluginManagement {
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
}

rootProject.name="DroidMedia"
include (":app")
//include (":exoplayer")
include (":awesomelib")
include (":medialibs:filterLibrary")
include (":medialibs:playerLib")
include (":medialibs:epf")
include (":medialibs:playerLib")
include (":medialibs:cameraLib")
include (":medialibs:mediakit")
include (":medialibs:ijksource")
include (":medialibs:yuv")
include (":medialibs:extractframe")
include (":medialibs:gpuv")
include (":medialibs:renderscript-toolkit")
//include (":medialibs:VidCompose")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


//  // 第二种方式使用版本目录
//    libs {
//        from(files("./libs.versions.toml"))
//    }
include (":serviceloaderapi:serviceapi")
include (":medialibs:mediainfo")

// https://docs.gradle.org/current/userguide/fine_tuning_project_layout.html
include (":mango:nativecrash")
project(":mango:nativecrash").projectDir = file("mango/nativecrash")
project(":mango:nativecrash").buildFileName = "project-a.gradle" // modify elements of projectDescriptor
include (":medialibs:jpegturbo")
include (":medialibs:audiolib")
include (":medialibs:pnglib")
include (":medialibs:pnglib")
include (":medialibs:videocache")
include (":medialibs:avif")
include (":mango:simdjson")
include(":medialibs:composeworkmanager",
    ":medialibs:aars:libav1decoder",
    ":medialibs:aars:libvp9decoder")

includeBuild("carica")
