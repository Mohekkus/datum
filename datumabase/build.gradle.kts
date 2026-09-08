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

    val room_version = "3.0.2"
    implementation("androidx.room3:room3-runtime:$room_version")
    ksp("androidx.room3:room3-compiler:$room_version")

    implementation(libs.kotlinx.serializationJson)
}

tasks.test {
    useJUnitPlatform()
}