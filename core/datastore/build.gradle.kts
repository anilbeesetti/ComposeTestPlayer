plugins {
    id("nextplayer.android.library")
    id("nextplayer.android.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.arcticoss.nextplayer.core.datastore"
}

dependencies {

    implementation(project(":core:model"))

    implementation(libs.androidx.core.ktx)

    // Datastore
    implementation(libs.androidx.dataStore.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}