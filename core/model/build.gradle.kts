plugins {
    id("kotlin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation (libs.kotlinx.serialization.json)
}