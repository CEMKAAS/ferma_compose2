import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
    id("io.appmetrica.analytics")
}

android {
    namespace = "com.zaroslikov.fermacompose2"
    compileSdk = 36

    defaultConfig {
        val buildTime = System.currentTimeMillis()

        buildConfigField(
            "long",
            "BUILD_TIME",
            buildTime.toString()
        )

        applicationId = "com.zaroslikov.fermacompose2"
        minSdk = 26
        targetSdk = 36
        versionCode = 18 //12
        versionName = "beta-v3.0.3v"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
           /* signingConfig = signingConfigs.getByName("debug")*/
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    appmetrica {
        postApiKey.set("7bc20e66-fc56-4002-ac33-4cc15dd28213")
        offline.set(true)
        enable.set(!isDebugBuild())
        enableAnalytics.set(true)
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    // Icon
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Import the Compose BOM
    implementation(platform("androidx.compose:compose-bom:2025.10.00"))
    implementation("androidx.activity:activity-compose:1.11.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.navigation:navigation-compose:2.9.5")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("androidx.core:core-splashscreen:1.2.0")

    //Room
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.compose.animation.core)
    testImplementation("org.testng:testng:6.9.6")
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")

    // Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // Ads
    implementation(libs.mobileads)
    implementation(libs.mobileads.compose)

    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // AppMetrica SDK.
    implementation(libs.analytics)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)

    //RuStore SDK
    implementation(libs.appupdate)

}

fun isDebugBuild(): Boolean {
    return gradle.startParameter.taskNames.any { it.contains("Debug") }
}