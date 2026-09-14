import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":occt"))
    implementation(project(":database"))
    implementation(project(":contracts"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.kotlinx.serializationJson)
}

compose.desktop {
    application {
        mainClass = "cc.shinemoon.datum.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "Datum"
            packageVersion = "0.1.0"
        }
    }
}
