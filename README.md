# Datum 0.1.0

Datum is an early Windows desktop application for inspecting CAD/OCCT-derived model data
and evaluating it against user-defined presets.

This is an initial public release. The core inspection and preset workflow is usable, but
the application is still evolving and may contain rough edges or breaking changes.

## Platform Support

Datum currently targets Windows desktop. The OCCT bridge is bundled as a Windows `.dll`, so other operating systems are not supported yet.

## Current Limitations

- Windows is currently the supported platform.
- The OCCT bridge is currently distributed as a Windows DLL.
- Explorer context-menu integration, batch inspection, PDF export, and automatic updates
  are not included yet.
- Presets are stored locally on the current machine and are not synchronized.

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

The packaged Windows installer can be created with:

`./gradlew :desktopApp:packageMsi`

## Running Tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Desktop tests: `./gradlew :shared:jvmTest`

## Data Location

Preset data is stored locally in the user's app data directory:

- Windows: `%APPDATA%\Datum\datumabase.db`
- Fallback: `<user home>/.datum/Datum/datumabase.db`

## Development Notes

The repository includes the Gradle wrapper, so contributors can use the commands above
without installing a separate Gradle distribution. The bundled OCCT native library means
that the current desktop workflow is Windows-specific.
