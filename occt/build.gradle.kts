plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
