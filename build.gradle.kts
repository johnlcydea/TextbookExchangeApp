// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false // ✅ Used latest version
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false // ✅ KSP correctly placed
    id("com.android.library") version "8.2.2" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir) // ✅ Corrected `clean` task

}

buildscript {
repositories {
google()
mavenCentral()
}
}