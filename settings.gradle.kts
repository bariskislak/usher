pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "usher"

include(
    "common",
    "auth-service",
    "url-service",
    "gateway",
    "analytics-service",
    "notification-service",
)
