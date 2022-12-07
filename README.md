# Next Player

Android Native video player based on exoplayer built with jetpack compose.

It uses Exoplayer's ``ffmpeg`` extension with all formats enabled.


## Build

To build the project, you need to have the following installed: 
git, Android Studio, and the Android SDK.

1. Clone the Next Player repository:
```bash
git clone https://github.com/anilbeesetti/NextPlayer.git
cd NextPlayer
```
2. To build the ffmpeg, run the following command from the root of the project:
```bash
./mediainfo/src/main/ffmpeg/ffmpeg-android-maker.sh
```
4. To build the project, run the following command from the root of the project:
```bash
./gradlew assembleDebug
```
6. Enjoy
