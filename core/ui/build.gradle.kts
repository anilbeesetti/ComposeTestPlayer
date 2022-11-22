plugins {
    id("nextplayer.android.library")
    id("nextplayer.android.library.compose")
}

android {
    namespace = "com.arcticoss.nextplayer.core.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.activity.compose)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.iconsExtended)
    debugApi(libs.androidx.compose.ui.tooling)
}