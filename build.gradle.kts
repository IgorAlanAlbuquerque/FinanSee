// In your ROOT build.gradle.kts
plugins {
    // Use only one declaration for the Android Application plugin
    id("com.android.application") version "8.4.1" apply false

    // Use only the NEW version of the Kotlin plugin
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false

    // This KSP version matches your new Kotlin version
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false

    // This one is fine
    id("com.google.gms.google-services") version "4.3.15" apply false

    id("org.jetbrains.compose") version "1.6.10" apply false

}