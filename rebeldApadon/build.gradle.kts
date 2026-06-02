plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinAndroidMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "io.angol.rebeld.apadon"
        compileSdk = 35
        minSdk = 26
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":angolModyil"))
            implementation(project(":kepadModyil"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
