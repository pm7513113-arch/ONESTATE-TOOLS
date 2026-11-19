# ONESTATE TOOLS — Mini Edition

Android Studio project for a compact black/red floating developer panel.

## Compatibility
- Minimum Android: 14 (API 34)
- Target / compile SDK: 36 (Android 16)
- Kotlin + Android Gradle Plugin

## Current prototype
- Requests Android's draw-over-other-apps permission.
- Starts a foreground service and shows a draggable mini panel.
- Includes demo switches for ESP Debug, Aim Test, Player Boxes, and Health/Distance.
- The switches are UI-only placeholders. They do not read game memory, inspect other apps, or affect gameplay.

## Build
Open this folder in Android Studio with Android SDK 36 installed, allow Gradle sync, then choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

The APK is not included because this environment does not have the Android SDK or Gradle build tool installed. The project source is ready for Android Studio; test the overlay on a device you own.
