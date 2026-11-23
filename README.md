# Classroom X

An independent Android companion client for Google Classroom, built with Kotlin, Jetpack Compose, and Material 3.

> Classroom X is an unofficial third-party project and is not affiliated with Google. It uses original UI and supported public integrations; it does not copy Google's proprietary source code or private app internals.

## Product direction

The goal is to cover the main student and teacher workflows familiar from Google Classroom: courses, stream, classwork, topics, assignments, submissions, feedback, grades, people, planner, notifications, and settings. Exact parity is not possible for every first-party feature because the public Classroom API does not expose every feature and some capabilities require separate Google APIs, Workspace editions, or role-specific permissions.

See:
- [Feature parity specification](docs/FEATURE_PARITY.md)
- [Google Classroom API capability notes](docs/GOOGLE_CLASSROOM_API.md)

## Current status

**Early UI prototype.** The repository currently contains a starter dashboard and Android Compose scaffold. No Google account sign-in, live Classroom data, assignment submission, notifications backend, or AI provider is connected yet.

## Stack

- Kotlin
- Jetpack Compose + Material 3
- MVVM / repository-oriented architecture
- Min Android API 26
- JDK 17

## Build

Open in a compatible Android Studio installation with JDK 17 and the Android SDK installed. A Gradle wrapper still needs to be added/generated before command-line builds are reproducible.

## Roadmap

1. Foundation: navigation, design system, preview/demo data, state handling.
2. Read-only Classroom API: courses, classwork, stream, topics, submissions and grades.
3. Student workflows: assignment details, turn-in/reclaim and attachments where supported.
4. Teacher workflows: authoring and grading with explicit role checks and write scopes.
5. Planner, local notifications, then push / Drive / Calendar integrations.
6. X AI backend, privacy controls and provider routing.
7. Accessibility, performance, X Smoothness and later Liquid Glass experiments.

## Security

Never commit API keys, OAuth client secrets, access tokens, service-account keys, or real student data. Keep AI provider keys on a backend. Start with read-only OAuth scopes and request write scopes only when the user enables the relevant feature.
