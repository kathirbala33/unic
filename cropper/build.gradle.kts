plugins {
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlin.android")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.vanniktech.maven.publish")
    id("app.cash.licensee")
    id("app.cash.paparazzi")
}

licensee {
    allow("Apache-2.0")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

android {
    namespace = "com.canhub.cropper"

    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.exifinterface:exifinterface:1.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
}

dependencies {
    testImplementation("androidx.fragment:fragment-testing:1.6.1")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.robolectric:robolectric:4.9")
}
