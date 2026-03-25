pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Terra"
include(":app")

// core
include(":core:designsystem")
include(":core:network")
include(":core:data:country")
include(":core:data:favourites")
include(":core:domain:common")
include(":core:domain:country")
include(":core:domain:favourites")

// features
include(":feature:welcome")
include(":feature:home")
include(":feature:details")
