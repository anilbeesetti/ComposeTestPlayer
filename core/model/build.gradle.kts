plugins {
    id ("java-library")
    kotlin("jvm")
    id ("org.jetbrains.kotlin.plugin.serialization")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}