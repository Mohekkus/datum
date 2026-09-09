plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    implementation(libs.dagger)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
