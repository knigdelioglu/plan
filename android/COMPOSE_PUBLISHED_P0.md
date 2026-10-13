# P0: versioned Compose artifact — Plan opt-in

The default source checkout is intentionally unchanged. Plan currently pins `f43ef2d0302fee1d5cfa50bd089441f96fe21018` via `.github/compose-design-system.ref` and compiles Compose sources within the app.

Once Compose publishes a validated AAR version (for example `0.1.0`), switch a **test build only** to:

```sh
cd android
./gradlew testDebugUnitTest assembleDebug assembleDebugAndroidTest -PcomposePublishedVersion=0.1.0
```

This avoids `kotlin.directories.add(.../compose-design-system/src/main/java)` and instead resolves `com.knigdelioglu:compose-design-system:<version>` from the public Maven distribution branch. The repository is only included for these explicit artifact builds.

The normal builds and release workflows continue to enforce the existing pinned SHA until the binary compatibility smoke test has passed under Plan's AGP 9.4.0 / Kotlin 2.4.20 toolchain. Source checkout and Maven dependency **must never be active simultaneously**, to avoid duplicate `com.knigdelioglu.arc.compose` classes.

Before switching the default, run all three display matrices from `.github/workflows/android-ui-tests.yml`, assess the nested Plan/Compose theme bridge, and verify notes, calendar, search, week selection, font scaling and system bars. The Plan-specific adapter components remain in Plan; do not migrate its domain/data models into Compose.

Rollback: omit `-PcomposePublishedVersion` to restore the existing SHA checkout. Future default conversion should be an independently reviewed PR, after the published AAR has real green CI evidence.
