plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.cuisineconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cuisineconnect"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    packaging {
        resources {
            pickFirsts.add("META-INF/INDEX.LIST")
            pickFirsts.add("META-INF/io.netty.versions.properties")
        }
    }
}


dependencies{
    implementation(libs.azure.translation)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.google.location)
    implementation(libs.picasso)
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}