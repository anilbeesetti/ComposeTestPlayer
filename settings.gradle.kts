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
}
rootProject.name = "NextPlayer"
include(":app")
include(":mediainfo")
include(":core:datastore")
include(":core:database")
include(":core:model")
include(":core:data")
include(":core:domain")
include(":feature:media")
include(":feature:player")
include(":feature:settings")
