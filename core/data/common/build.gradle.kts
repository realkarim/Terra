plugins {
    alias(libs.plugins.kotlin.android)
    id("com.android.library")
}

android {
    namespace = "com.realkarim.data.common"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(project(":core:domain:common"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}
