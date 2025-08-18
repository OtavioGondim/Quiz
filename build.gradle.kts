plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
}

buildscript {
    dependencies {
        // Se algum plugin pedir Gson no classpath
        classpath("com.google.code.gson:gson:2.13.1")
    }
}