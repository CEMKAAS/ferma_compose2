import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.appmetrica)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.zaroslikov.fermacompose2"
    compileSdk = 37

    signingConfigs {
        create("ferma-release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(File(keystoreProperties["storeFile"] as String))
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    defaultConfig {
        val buildTime = System.currentTimeMillis()

        buildConfigField(
            "long",
            "BUILD_TIME",
            buildTime.toString()
        )

        applicationId = "com.zaroslikov.fermacompose2"
        minSdk = 26
        targetSdk = 37
        versionCode = 22 //12
        versionName = "v3.1.1v"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("ferma-release")
            /* signingConfig = signingConfigs.getByName("debug")*/
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// kotlin и appmetrica — расширения уровня Project, а не android {}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

appmetrica {
    postApiKey.set("7bc20e66-fc56-4002-ac33-4cc15dd28213")
    offline.set(true)
    enable.set(!isDebugBuild())
    enableAnalytics.set(true)
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    // :app напрямую использует CoroutineWorker/Configuration. Provider
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.kotlinx.serialization.json)

    // Room: :app использует только ColumnInfo и withTransaction,
    // своих @Entity/@Dao здесь нет — room-compiler не нужен
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler)

    // Ads
    implementation(libs.mobileads)
    implementation(libs.mobileads.compose)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // AppMetric SDK
    implementation(libs.analytics)

    // RuStore SDK
    implementation(libs.appupdate)

    // QR-code
    implementation(libs.zxing.core)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

fun isDebugBuild(): Boolean {
    return gradle.startParameter.taskNames.any { it.contains("Debug") }
}