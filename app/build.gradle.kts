plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.test_api"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.test_api"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    // 🔧 Выносим адрес сервера в конфиг
    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.X:8000\"") // эмулятор
            // Для телефона с тем же ПК: поменяй на "\"http://192.168.1.X:8000\""
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.X:8000\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}