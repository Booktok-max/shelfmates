import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.shelfmates"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  buildFeatures {
   buildConfig = true
   }

  defaultConfig {
    applicationId = "com.aistudio.shelfmates.readery"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    val googleBooksApiKey = providers.environmentVariable("GOOGLE_BOOKS_KEY")
        .orElse(providers.gradleProperty("GOOGLE_BOOKS_KEY"))
        .getOrElse("")
    buildConfigField("String", "GOOGLE_BOOKS_KEY", "\"$googleBooksApiKey\"")
    val geminiApiKey = providers.environmentVariable("GEMINI_API_KEY")
        .orElse(providers.gradleProperty("GEMINI_API_KEY"))
        .getOrElse("MY_GEMINI_API_KEY")
    buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    val nytBooksApiKey = providers.environmentVariable("NYT_BOOKS_KEY")
      .orElse(providers.gradleProperty("NYT_BOOKS_KEY"))
      .getOrElse("")
    buildConfigField("String", "NYT_BOOKS_KEY", "\"$nytBooksApiKey\"")

    // ── Production service configuration ────────────────────────────────────
    // Supabase Storage. These default to EMPTY on purpose: an empty value means
    // "not configured" and is handled explicitly at runtime. No credential is
    // ever fabricated or hard-coded as a fallback. Supply them via environment
    // variable or Gradle property (see .env.example).
    val supabaseUrl = providers.environmentVariable("SUPABASE_URL")
      .orElse(providers.gradleProperty("SUPABASE_URL"))
      .getOrElse("")
    buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
    val supabaseAnonKey = providers.environmentVariable("SUPABASE_ANON_KEY")
      .orElse(providers.gradleProperty("SUPABASE_ANON_KEY"))
      .getOrElse("")
    buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
    val supabaseBucket = providers.environmentVariable("SUPABASE_BUCKET")
      .orElse(providers.gradleProperty("SUPABASE_BUCKET"))
      .getOrElse("")
    buildConfigField("String", "SUPABASE_BUCKET", "\"$supabaseBucket\"")

    // OAuth 2.0 *web* client ID (not the Android client ID) used as the
    // server_client_id for Google Identity Services. Required for Google
    // Sign-In; defaults to empty so a missing value fails closed and reports a
    // configuration error instead of silently downgrading the session.
    val googleWebClientId = providers.environmentVariable("GOOGLE_WEB_CLIENT_ID")
      .orElse(providers.gradleProperty("GOOGLE_WEB_CLIENT_ID"))
      .getOrElse("")
    buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      isReturnDefaultValues = true
    }
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Firestore:
  implementation(libs.firebase.firestore)

  // Firebase Auth and Google Sign-In via Credential Manager:
  implementation(libs.firebase.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  implementation(libs.folioreader.core) {
    exclude(group = "xmlpull", module = "xmlpull")
  }
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

tasks.matching { it.name.startsWith("ksp") && it.name.contains("UnitTest") }.configureEach {
  enabled = false
}
