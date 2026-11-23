# Building Classroom X

## Current state

Classroom X is an early Android application scaffold. The current Compose screens are a UI prototype and still use local demonstration data. Google sign-in, Classroom synchronization, remote repositories, and real assignment submission are not implemented yet.

## CI build

Every push to `main`, pull request targeting `main`, or manual workflow dispatch runs the Android debug build in GitHub Actions.

The workflow:
1. Sets up JDK 17 and the Android SDK.
2. Installs Gradle 9.5.0.
3. Generates a temporary Gradle Wrapper (the wrapper bootstrap is not yet committed).
4. Runs `./gradlew :app:assembleDebug`.
5. Publishes `app-debug.apk` as the `classroom-x-debug` workflow artifact for 14 days.

Open the repository's **Actions** tab, select **Android CI**, open a successful run, and download the `classroom-x-debug` artifact.

## Local build

Requirements: JDK 17, Android SDK, and Gradle 9.5.0.

```bash
gradle wrapper --gradle-version 9.5.0
./gradlew :app:assembleDebug
```

The APK is written to:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Important

A successful debug build only verifies compilation and packaging. It does not mean Classroom API integration or production readiness. Never commit OAuth client secrets, signing keys, or local SDK paths.
