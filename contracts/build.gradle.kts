plugins {
    kotlin("jvm") version "2.4.10"
}

group = "cc.shinemoon"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}