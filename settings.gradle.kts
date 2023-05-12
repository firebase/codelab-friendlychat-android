pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":build-android:app",
        ":build-android-start:app"
)
