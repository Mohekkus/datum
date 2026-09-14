plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)

    id("dev.nucleusframework") version "2.5.15"
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

    implementation("dev.nucleusframework:nucleus.nucleus-application:2.5.15")
    implementation("dev.nucleusframework:nucleus.decorated-window-tao:2.5.15")
}

nucleus {
    application {
        mainClass = "cc.shinemoon.datum.MainKt"

        graalvm {
            isEnabled.set(true)
            buildArgs.add("-H:+AddAllCharsets")
        }
    }
}
