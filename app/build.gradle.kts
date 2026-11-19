plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.onestate.tools"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.onestate.tools"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}
