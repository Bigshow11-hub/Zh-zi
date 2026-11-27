plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.zhuzi.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zhuzi.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 39
        versionName = "2.5.1"
    }

    // Clé de test fixe (sans secret) : chaque nouvelle version s'installe par-dessus l'ancienne.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            // Toutes les signatures (v1 + v2 + v3) : certains téléphones Samsung les vérifient toutes
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }

    // Photos d'écran faites sur GitHub (Robolectric + Roborazzi) pour vérifier le design
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { it.systemProperty("roborazzi.test.record", "true") }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.work.runtime.ktx)
    // Lecture du texte chinois sur une photo (sur le téléphone, sans internet) et pinyin automatique
    implementation(libs.mlkit.text.chinese)
    // Téléchargement explicite du module de lecture (indispensable hors Play Store)
    implementation(libs.play.services.base)
    implementation(libs.pinyin4j)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit4)
}
