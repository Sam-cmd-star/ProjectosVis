plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // ESTA LÍNEA ES LA CLAVE: Habilita el procesador de anotaciones para Room
    id("kotlin-kapt")
}

android {
    namespace = "com.example.ecocleanmanager"
    // Subimos a 35 para dar soporte a librerías modernas
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ecocleanmanager"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // 1. Configuración de Room (Base de Datos Local para ECOLIM)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version") // Ahora kapt será reconocido
    implementation("androidx.room:room-ktx:$room_version")

    // 2. Consumo de API RESTful (Sincronización remota) [cite: 8]
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 3. Ajustes de compatibilidad (Evita el error de SDK 36)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // 4. Librerías de Interfaz y Diseño
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// ELIMINA la función fun kapt(s: String) { } que tenías al final,
// ya no es necesaria con el plugin id("kotlin-kapt")