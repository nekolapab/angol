plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget()
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":angolKor"))
            implementation("org.jetbrains.compose.runtime:runtime:1.7.0")
            implementation("org.jetbrains.compose.foundation:foundation:1.7.0")
            implementation("org.jetbrains.compose.material:material:1.7.0")
            implementation("org.jetbrains.compose.ui:ui:1.7.0")
            implementation("org.jetbrains.compose.components:components-resources:1.7.0")
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "io.angol.kepad.modyil"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
}
