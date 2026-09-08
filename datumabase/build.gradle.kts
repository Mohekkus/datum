plugins {
    alias(libs.plugins.kotlinJvm)
    id("com.google.devtools.ksp") version "2.3.10"
    alias(libs.plugins.kotlinSerialization)
}

group = "cc.shinemoon.datumabase"
version = "1"

repositories {
    mavenCentral()

    google {
        mavenContent {
            includeGroupAndSubgroups("androidx")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.androidx.sqlite.bundled.jvm)
    implementation(libs.androidx.room3.runtime.jvm)
    ksp(libs.androidx.room3.compiler)
    implementation(libs.kotlinx.serializationJson)
}

tasks.test {
    useJUnitPlatform()
}