plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.angol.ime"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.angol.ime"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Build variants for WearOS vs Android
    flavorDimensions += "platform"
    productFlavors {
        create("wear") {
            dimension = "platform"
            applicationIdSuffix = ".wear"
            minSdk = 25
        }
        create("mobile") {
            dimension = "platform"
            applicationIdSuffix = ".mobile"
            minSdk = 25
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
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(project(":kepad"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")

    // Shared Compose dependencies
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Platform-specific dependencies
    "wearApi"(platform("androidx.compose:compose-bom:2024.05.00"))
    "wearApi"("androidx.compose.ui:ui-graphics")
    "wearApi"("androidx.compose.foundation:foundation")
    "wearApi"("androidx.compose.material:material")
    // Wear OS Compose
    "wearApi"("androidx.wear.compose:compose-material:1.3.1")
    "wearApi"("androidx.wear.compose:compose-foundation:1.3.1")

    "mobileApi"(platform("androidx.compose:compose-bom:2024.05.00"))
    "mobileApi"("androidx.compose.ui:ui-graphics")
    "mobileApi"("androidx.compose.foundation:foundation")
    "mobileApi"("androidx.compose.material:material")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
