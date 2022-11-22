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
    implementation(project(":core:datastore"))

    implementation(libs.accompanist.permissions)

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)

}