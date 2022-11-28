plugins {
    id("nextplayer.android.feature")
    id("nextplayer.android.library.compose")
}

android {
    namespace = "com.arcticoss.nextplayer.feature.media"
}

dependencies {

    implementation(project(":mediainfo"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))

    implementation(libs.accompanist.permissions)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)

}