plugins {
    id("nextplayer.android.feature")
    id("nextplayer.android.library.compose")
}

android {
    namespace = "com.arcticoss.nextplayer.feature.settings"
}

dependencies {

    implementation(project(":core:model"))
    implementation(project(":core:data"))

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}