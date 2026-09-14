# Datum

Datum is a Kotlin Multiplatform desktop app for inspecting CAD/OCCT-derived model data.

## Platform Support

Datum currently targets Windows desktop. The OCCT bridge is bundled as a Windows `.dll`, so other operating systems are not supported yet.

Preset data is stored locally in the user's app data directory:

- Windows: `%APPDATA%\Datum\datumabase.db`
- Fallback: `<user home>/.datum/Datum/datumabase.db`

## Project Layout

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
      folder is the appropriate location.

## Running The App

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and
options:

- Desktop app:
    - Hot reload: `./gradlew :desktopApp:hotRun --auto`
    - Standard run: `./gradlew :desktopApp:run`

## Running Tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Desktop tests: `./gradlew :shared:jvmTest`
