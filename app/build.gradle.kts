@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "org.miui.refine"
    compileSdk = 33

    defaultConfig {
        applicationId = "org.miui.refine"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        aidl = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    val nav_version = "2.7.0-beta01"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    compileOnly("de.robv.android.xposed:api:82")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

    // toolchain
    val libsuVersion = "5.1.0"
    implementation("com.github.topjohnwu.libsu:core:${libsuVersion}")
    implementation("com.github.topjohnwu.libsu:io:${libsuVersion}")


    implementation("com.google.accompanist:accompanist-insets:0.31.4-beta")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.4-beta")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.31.4-beta")


    val lottieVersion = "6.0.1"
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    // logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // startup
    implementation("androidx.startup:startup-runtime:1.1.1")
    // https://mvnrepository.com/artifact/org.simpleframework/simple-xml
    implementation("org.simpleframework:simple-xml:2.7.1")



}