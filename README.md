# Classroom X

An independent Android companion client for Google Classroom, built with Kotlin and Jetpack Compose.

> Classroom X is an unofficial third-party project and is not affiliated with Google.

## Current status

**Scaffold / UI prototype.** The first milestone establishes the Android app structure and a local dashboard preview. Google OAuth, Classroom API integration, AI providers, notifications, and persistence are not connected yet.

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM-oriented feature structure
- Minimum Android version: API 26 (Android 8.0)
- Compile / target SDK: API 37

## Project structure

```
app/
  src/main/
    java/com/classroomx/
      MainActivity.kt
      core/
      features/
    AndroidManifest.xml
gradle/
  libs.versions.toml
```

## Build

Open this repository in a compatible Android Studio version with JDK 17 and Android SDK 37 installed. Generate a Gradle wrapper if one is not present, then sync and build the `app` configuration.

## Roadmap

- [x] Initial Android / Compose scaffold
- [x] First local dashboard prototype
- [ ] Navigation and settings
- [ ] X Smoothness (adaptive display mode request)
- [ ] Google OAuth and Classroom API (read-only first)
- [ ] Local cache and notifications
- [ ] AI provider gateway
- [ ] Security and privacy review

## Privacy and security

Never commit API keys, OAuth client secrets, access tokens, or real student data. AI requests should go through a backend rather than embedding provider secrets in the APK.
