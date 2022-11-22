plugins {
    id("nextplayer.android.feature")
    id("nextplayer.android.library.compose")
}

android {
    namespace = "com.arcticoss.nextplayer.feature.player"
}

dependencies {

    implementation (project(":core:model"))
    implementation (project(":core:datastore"))
    implementation (project(":core:domain"))
    implementation (project(":core:data"))

    implementation ("com.google.android.exoplayer:exoplayer-core:2.18.1")

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)
}