import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

// GitHub Actions passes an explicitly verified checkout location. Local builds
// default to an isolated, pinned cache rather than an arbitrary sibling checkout.
val explicitComposeSources = providers.gradleProperty("composeDesignSystemSources").orNull
val preparedComposeSources = rootProject.file(".compose-deps/compose-design-system/design-system/src/main/java")
val siblingComposeSources = rootProject.file("../../compose-design-system/design-system/src/main/java")
val composeDesignSystemSourceDir = when {
    explicitComposeSources != null -> rootProject.file(explicitComposeSources)
    preparedComposeSources.isDirectory -> preparedComposeSources
    else -> siblingComposeSources
}
check(composeDesignSystemSourceDir.isDirectory) {
    "Compose design system sources missing at ${composeDesignSystemSourceDir.absolutePath}. " +
        "Run: bash android/scripts/prepare-compose-design-system.sh, or supply -PcomposeDesignSystemSources=<path>."
}

// Fail closed for implicit local sources: otherwise a successful local APK could
// silently use a different component revision than the published/CI APK.
if (explicitComposeSources == null) {
    val pinnedRevision = rootProject.file("../.github/compose-design-system.ref").readText().trim()
    check(Regex("[a-f0-9]{40}").matches(pinnedRevision)) {
        "Invalid Compose revision in .github/compose-design-system.ref"
    }
    val checkout = composeDesignSystemSourceDir.parentFile.parentFile.parentFile.parentFile
    val actualRevision = runCatching {
        val process = ProcessBuilder("git", "-C", checkout.absolutePath, "rev-parse", "HEAD")
            .redirectErrorStream(true)
            .start()
        val result = process.inputStream.bufferedReader().use { it.readText().trim() }
        if (process.waitFor() == 0) result else null
    }.getOrNull()
    check(actualRevision == pinnedRevision) {
        "Local Compose checkout ${checkout.absolutePath} is ${actualRevision ?: "unknown"}, " +
            "but Plan is pinned to $pinnedRevision. " +
            "Run: bash android/scripts/prepare-compose-design-system.sh " +
            "or explicitly provide -PcomposeDesignSystemSources=<path> for intentional local development."
    }
}

android {
    namespace = "com.plan.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.plan.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 6
        versionName = "1.0.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            assets.directories.add("../../plans")
            kotlin.directories.add(composeDesignSystemSourceDir.absolutePath)
        }
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20260814")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
