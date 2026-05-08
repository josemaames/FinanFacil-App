plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    kotlin("kapt")
}

android {

    namespace = "upch.jamesss.finanfacil"

    compileSdk = 36

    defaultConfig {

        applicationId = "upch.jamesss.finanfacil"

        minSdk = 24
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {

        jvmTarget = "11"
    }

    buildFeatures {

        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation("androidx.appcompat:appcompat:1.7.0")

    // MATERIAL DESIGN
    implementation("com.google.android.material:material:1.12.0")

    // ROOM DATABASE
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    kapt("androidx.room:room-compiler:2.7.0")

    // LIFECYCLE
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2"
    )

    implementation(
        "androidx.lifecycle:lifecycle-livedata-ktx:2.8.2"
    )

    // RECYCLERVIEW
    implementation(
        "androidx.recyclerview:recyclerview:1.3.2"
    )

    // MPANDROIDCHART
    implementation(
        "com.github.PhilJay:MPAndroidChart:v3.1.0"
    )

    // GLIDE
    implementation(
        "com.github.bumptech.glide:glide:4.16.0"
    )

    kapt(
        "com.github.bumptech.glide:compiler:4.16.0"
    )

    // TESTS
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(
        libs.androidx.espresso.core
    )
}