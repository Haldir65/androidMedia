pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
    versionCatalogs {
        create("libs") {
            from(files(rootDir.parentFile.parentFile.resolve("gradle/libs.versions.toml")))
        }
    }
}

rootProject.name = "Vid Compose"
include(":app")
