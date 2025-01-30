pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google() // ✅ Required for Firebase & AndroidX
        mavenCentral()
    }
}

rootProject.name = "TextbookExchangeApp"

// ✅ Ensure Firebase Modules are included
include(":app")
