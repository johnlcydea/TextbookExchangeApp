// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.android.application") version "8.1.1" apply false
}

// ✅ Corrected Clean Task using `layout.buildDirectory`
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
