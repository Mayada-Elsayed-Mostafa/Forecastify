plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}
secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "local.defaults.properties"
}

android {
    namespace = "com.example.forecastify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.forecastify"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Scoped API for ViewModels with Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")

    // LiveData & Compose Integration
    val composeVersion = "1.0.0"
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")

    // Coroutines Support
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    // Networking - Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // JSON Processing - Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Logging for Networking - OkHttp Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Image Loading - Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Database - Room Persistence Library
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Image Loading with Coil
    implementation("io.coil-kt:coil-compose:2.0.0")

    // Navigation - Jetpack Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.8.8")

    // Serialization for Navigation Arguments
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Custom Fonts for Compose UI
    implementation("androidx.compose.ui:ui-text-google-fonts:1.4.0")

    // Compose Material 3 Adaptive
    implementation("androidx.compose.material3.adaptive:adaptive:1.2.0-alpha01")
    implementation("androidx.compose.material3.adaptive:adaptive-layout:1.2.0-alpha01")
    implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.2.0-alpha01")

    //Jetpack DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //For Location
    implementation("com.google.android.gms:play-services-location:21.1.0")

    //Work Manager
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    //Map
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.maps.android:maps-compose:2.11.4")

    //LottieFile
    implementation("com.airbnb.android:lottie-compose:6.1.0")

    // Dependencies for local unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.5.1")

    // InstantTaskExecutorRule
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    //kotlinx-coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")

    //MockK
    testImplementation("io.mockk:mockk-android:1.13.17")
    testImplementation("io.mockk:mockk-agent:1.13.17")

    // AndroidX Test - JVM testing
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation("androidx.test.ext:junit:1.1.3")

    // AndroidX Test - Instrumented testing
    androidTestImplementation("androidx.test:core:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    //Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // hamcrest
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    androidTestImplementation("org.hamcrest:hamcrest:2.2")
    androidTestImplementation("org.hamcrest:hamcrest-library:2.2")


    // AndroidX and Robolectric
    testImplementation("androidx.test.ext:junit-ktx:1.1.3")
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation("org.robolectric:robolectric:4.11")


}