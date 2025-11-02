import java.util.Properties
import kotlin.apply

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  alias(libs.plugins.kotlin.compose)
}

// Read local.properties file
val localProperties = Properties().apply {
  val localPropertiesFile = rootProject.file("local.properties")
  if (localPropertiesFile.exists()) {
    load(localPropertiesFile.inputStream())
  }
}

android {
  namespace = "com.volleylord.core"
  compileSdk = 36

  defaultConfig {
    minSdk = 24

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
    buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("API_KEY", "")}\"")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  implementation(project(":core_domain"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui.graphics)

  // Paging
  implementation(libs.androidx.paging.runtime)

  // Storage
  implementation(libs.androidx.datastore)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.runtime)
  ksp(libs.androidx.room.compiler)

  // Ktor
  implementation(libs.ktor)
  implementation(libs.ktor.core)
  implementation(libs.ktor.serialization)
  implementation(libs.ktor.logging)
  implementation(libs.ktor.content.negotiation)

  // Coroutines
  implementation(libs.coroutines)
  implementation(libs.coroutines.android)

  // Serialization
  implementation(libs.kotlin.serialization.json)

  // Hilt
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  implementation(libs.androidx.hilt.navigation.compose)

  // Crypto
  implementation(libs.security.crypto)

  // Test
  testImplementation(libs.mockk.android)
  testImplementation(libs.junit)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.turbine)
  testImplementation(libs.robolectric)

  androidTestImplementation(libs.mockk.android)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)

  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}