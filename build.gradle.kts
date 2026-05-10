buildscript {
    extra.apply {
        set("room_version", "2.6.0")
    }
}

plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10" apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    id("io.appmetrica.analytics") version "2.0.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)  // ← Изменено здесь
}


