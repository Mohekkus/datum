plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()


    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10") // or latest multiplatform-compatible version

            //Simple Icon
            implementation("br.com.devsrsouza.compose.icons:simple-icons:1.1.1")

            //Feather Icon
            implementation("br.com.devsrsouza.compose.icons:feather:1.1.1")

            //Viewmodel
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

            //Coroutine
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

            implementation(project(":occt"))
        }
        jvmMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}