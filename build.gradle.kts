plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    // Плагина org.jetbrains.kotlin.android здесь нет: AGP 9 компилирует Kotlin сам,
    // а этот плагин несовместим с новым DSL AGP 9.
    // Чисто JVM-модуль :domain по-прежнему использует kotlin.jvm.
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.kotlinCompose) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.appmetrica) apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}