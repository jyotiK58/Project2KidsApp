plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.learningapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.learningapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

configurations {
    implementation {
        exclude(group = "com.intellij", module = "annotations")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.room.compiler)
    implementation(libs.kotlin.stdlib)
    implementation(libs.volley)
    implementation(libs.lottie)
    implementation(libs.picasso)
    implementation(libs.jsoup)
    implementation(libs.support.annotations)
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation(libs.androidx.recyclerview.recyclerview)
    implementation(libs.exoplayer)
    implementation(libs.androidx.cardview)
    implementation(libs.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)






   }












