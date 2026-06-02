plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinAndroidMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "io.angol.modyil"
        compileSdk = 35
        minSdk = 24
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.7.0")
            implementation("org.jetbrains.compose.foundation:foundation:1.7.0")
            implementation("org.jetbrains.compose.material:material:1.7.0")
            implementation("org.jetbrains.compose.ui:ui:1.7.0")
            implementation("org.jetbrains.compose.components:components-resources:1.7.0")
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.firebase:firebase-auth:23.1.0")
                implementation("com.google.firebase:firebase-firestore:25.1.1")
                implementation("com.google.android.gms:play-services-tasks:18.1.0")
            }
        }
    }
}
