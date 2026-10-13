# Compose Design System dependency policy

Plan pins a published Android AAR through `android/gradle.properties`:

```properties
composeDesignSystemVersion=0.1.0
```

Gradle resolves `com.knigdelioglu:compose-design-system:<version>` from the public Maven branch and no longer compiles the library Kotlin sources inside `:app` by default. The app-specific Arc adapters remain in Plan.

## Validation before accepting a new library version
- Library publication is gated on Compose JVM, API, Gallery, Plan screen-matrix, and Puanla tablet connected emulator suites.
- Raise the pinned version in a Plan PR after publication. Verify Android CI and compact, portrait-tablet, and landscape-tablet UI tests before merging.
- Signing and distribution remain a separate app release process. A library update must not silently change existing Plan APK binaries.

## Emergency rollback
The legacy pinned source revision in `.github/compose-design-system.ref` is retained for emergency recovery, not ordinary builds.

```sh
bash android/scripts/prepare-compose-design-system.sh
cd android
./gradlew :app:assembleDebug -PcomposeLegacySources=true
```

Alternatively set `-PcomposePublishedVersion=<previous verified Maven version>` to roll back the binary dependency without source injection. The emergency rollback must not rewrite Plan data or its domain models.

Android CI and UI workflows expose `compose_legacy_sources` as an explicit manual override. Normal builds always use the pinned published version.
