plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
//    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.yes.unic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yes.unic"
        minSdk = 21
        targetSdk = 34
        versionCode = 7
        versionName = "1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //password - compress
        //alis - android
        //pass - compress
    }
buildFeatures{
    dataBinding{
        enable = true
        viewBinding = true
    }
}

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
//    implementation(project(":cropper"))
//    implementation(project(":cropper"))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:+")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //ads
    implementation ("com.google.android.gms:play-services-ads:22.4.0")
    //dynamic
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    //glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    //Crop
    implementation("com.vanniktech:android-image-cropper:4.5.0")
//    implementation("com.isseiaoki:simplecropview:1.1.8")
//    implementation ("com.github.CanHub:Android-Image-Cropper:4.5.0")
//    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
    //image in KB

}