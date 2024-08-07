// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    alias(libs.plugins.codequalitytools)
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

buildscript {
    dependencies {
        classpath("org.gradle.android.cache-fix:org.gradle.android.cache-fix.gradle.plugin:2.7.2")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.8.20")
        classpath("com.android.tools.build:gradle:8.0.2")
         classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
        classpath("app.cash.licensee:licensee-gradle-plugin:1.7.0")
        classpath("app.cash.paparazzi:paparazzi-gradle-plugin:1.3.0")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.25.3")
    }
}
