buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")

        val navVersion = "2.5.0"
        //noinspection GradleDependency
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}