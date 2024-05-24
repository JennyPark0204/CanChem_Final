plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    //firebase id("com.android.application") <<이거도 추가하라고 나와있는데 안되길래 지우니까 되더라구요..?
    id("com.google.gms.google-services")
}

buildscript {
    repositories {
        google()
    }
}

android {
    namespace = "com.example.canchem"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.example.canchem"
        minSdk = 21
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

    //내거에서 추가
    buildFeatures{
        viewBinding = true;
        dataBinding = true;
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
    implementation("androidx.credentials:credentials:1.3.0-alpha03")
//    implementation ("androidx.credentials:credentials:<latest version>")
//    implementation ("androidx.credentials:credentials-play-services-auth:21.0.0")
//    implementation ("com.google.android.libraries.identity.googleid:googleid:<latest version>")
    //firebase auth
    implementation ("com.google.firebase:firebase-auth:21.0.0")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    //recyclerview
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0") // 문자열 응답을 처리하기 위한 의존성

    //내 레트로핏 버전
    //implementation ("com.squareup.retrofit2:comverter-gson:2.9.0")
    //implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-database-ktx:21.0.0")

    //okhttps3
    implementation (platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    implementation ("com.squareup.okhttp3:okhttp:4.9.2")
    implementation ("com.squareup.okhttp3:logging-interceptor")

    // DataStore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    /* Google OAuth2.0 API */
    implementation ("com.google.gms:google-services:4.3.15")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")

    /* 네이버 아이디 로그인 API 서비스*/
    implementation(files("libs/oauth-5.9.0.aar"))  // 네아로 SDK
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.legacy:legacy-support-core-utils:1.0.0")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("com.airbnb.android:lottie:3.1.0")

    //파이어베이스
    implementation(libs.firebase.database)

    //사진 크롭을 위한 라이브러리 추가
    implementation ("com.soundcloud.android:android-crop:1.0.1@aar")

    //Picasso 라이브러리 추가
    implementation("com.squareup.picasso:picasso:2.71828")

    //원래 있던 거
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}