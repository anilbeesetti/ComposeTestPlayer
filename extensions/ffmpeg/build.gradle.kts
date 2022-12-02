plugins {
    id("nextplayer.android.library")
}

android {
    namespace = "com.google.android.exoplayer2.ext.ffmpeg"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags.add("")
            }
            ndk {
                abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.18.1"
        }
    }
}


dependencies {

    implementation("androidx.core:core-ktx:1.9.0")

    implementation("com.google.android.exoplayer:exoplayer-core:2.18.1")
    compileOnly("org.checkerframework:checker-qual:3.27.0")
}