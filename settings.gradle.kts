// settings.gradle.kts

pluginManagement {
    repositories {
        google() // Mova para o topo
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Mova para o topo
        mavenCentral()
    }
}

rootProject.name = "Quiz"
include(":app")