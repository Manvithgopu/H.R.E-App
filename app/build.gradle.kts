plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.newvideorecording"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.newvideorecording"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // CameraX
    implementation ("androidx.camera:camera-core:1.2.3")
    implementation ("androidx.camera:camera-camera2:1.2.3")
    implementation ("androidx.camera:camera-lifecycle:1.2.3")
    implementation ("androidx.camera:camera-view:1.2.3")
    implementation ("androidx.camera:camera-extensions:1.3.4")

    // Video Recording
    implementation ("androidx.camera:camera-video:1.3.4")
    implementation ("androidx.core:core-ktx:1.8.0")

    // Navigation
    implementation ("androidx.navigation:navigation-compose:2.6.0")
//    implementation ("androidx.lifecycle:lifecycle-viewmodel-iossimulatorarm64:2.8.6")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")

    // ML Kit Face Detection
    implementation ("com.google.mlkit:face-detection:16.1.5")

    // Material
    implementation ("androidx.compose.material3:material3:1.3.0")

    // Main Screen
    implementation ("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("androidx.compose.material3:material3:1.1.1")


    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:21.1.0")  // Firebase Authentication
    implementation ("com.google.firebase:firebase-storage:20.1.0")  // Firebase Storage
    implementation ("com.google.firebase:firebase-database:20.3.0") // Firebase Realtime Database
    implementation ("com.google.firebase:firebase-storage-ktx:20.1.0")

    //
    implementation ("com.google.accompanist:accompanist-flowlayout:0.36.0")
    implementation ("androidx.activity:activity-compose:1.6.0") // Update as per the latest stable version




}