plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.game"
    compileSdk = 35

    //Habilitando o binding
    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.game"
        minSdk = 24
        targetSdk = 35
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
}
dependencies {
    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)  // CORRIGIDO: Agora referencia a dependência que existe no libs.versions.toml
    testImplementation(libs.room.testing)    // CORRIGIDO: Mudou de debugImplementation para testImplementation + referência correta

    // UI e Core
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}