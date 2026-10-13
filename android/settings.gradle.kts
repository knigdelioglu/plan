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
        if (providers.gradleProperty("composePublishedVersion").isPresent) {
            maven { url = uri("https://raw.githubusercontent.com/knigdelioglu/compose-design-system/maven-repository/") }
        }
    }
}

rootProject.name = "PlanApp"
include(":app")
