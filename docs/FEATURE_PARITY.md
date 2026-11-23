# Classroom X — feature parity specification

Classroom X is an independent Android client inspired by the workflows of Google Classroom. It may implement equivalent user-facing workflows, but must not copy Google's source code, private APIs, trademarks, proprietary assets, or pixel-identical branded UI. Use original Material 3 components and the official public APIs.

## Product principles

- Student-first experience, with role-aware capabilities for teachers where the public API and OAuth permissions allow them.
- Material 3 as the base design system. Liquid Glass is a later visual layer and must not leak into domain or data layers.
- Offline-tolerant read experience with explicit sync state.
- Least-privilege OAuth scopes; start read-only and request write scopes only when a feature is enabled.
- Never fake a successful Classroom action. Show unsupported, permission-denied, and sync-error states distinctly.
- Protect student data. No API secrets in the APK; provider secrets belong on a backend.

## Feature catalogue

### A. Account and onboarding
- Google sign-in and account picker.
- Explain requested OAuth scopes before consent.
- Multiple-account support and account switching.
- Profile / role / domain display where available.
- Sign out and revoke/disconnect flow.
- First-run permissions and privacy explanation.

### B. Classes
- List active, archived, and teaching/enrolled courses as permitted.
- Course cards with name, section, subject, room, teacher, color, and course image where available.
- Course details and course navigation.
- Join course by invite code is not assumed available through API; provide official Classroom hand-off if unsupported.
- Course notifications and mute preferences (local preferences unless a supported API exists).

### C. Class stream
- Announcements and stream posts.
- Post details, attachments, links, Drive files, YouTube links, and add-on attachments where accessible.
- Comments / class discussions only where the supported API and account permissions expose them.
- Sort and filter by course / recent activity.
- Mark unread/read locally; do not imply this changes Google Classroom state unless API supports it.

### D. Classwork
- Topics and topic ordering.
- Assignments, questions, quiz-assignment visibility where supported, and course materials.
- Assignment detail: instructions, points, due date, topic, attachments, teacher.
- Status filters: assigned, turned in, returned / graded where available.
- Submission detail, attached work, private comments / feedback where supported.
- Turn in, unsubmit / reclaim, and attach work only with the required scopes and supported endpoints.
- Google Docs / Sheets / Slides / Drive files should open through their supported apps or web URLs; editing is not reimplemented inside Classroom X.

### E. Grades and feedback
- Per-assignment grades and returned state where visible to the signed-in student.
- Teacher feedback and rubric feedback where API exposes it.
- Course grade summaries only where returned by API / permitted settings.
- Never infer or fabricate missing grades.

### F. People
- Teachers and classmates roster where permissions allow.
- Open email action via installed mail app where email addresses are available and policy permits.
- Do not expose private student information beyond the Classroom API permission model.

### G. Planner and calendar
- Upcoming, overdue, and completed work.
- Week / month agenda and due-date sorting.
- Local reminders.
- Optional Google Calendar integration is a separate OAuth/API integration; Classroom API data alone is not a full Google Calendar replacement.

### H. Notifications
- Local due-date reminders.
- Sync notifications and notification preferences.
- Server push / Classroom registrations only after a backend and Pub/Sub subscription flow exists; background polling must be conservative and respect Android background limits.
- Android 13+ notification permission handling.

### I. Search
- Search courses, assignments, materials, and announcements from locally synchronized data.
- Filters by course, status, due date, and topic.
- Clearly label results that have not synced yet.

### J. Settings and accessibility
- Account management, theme (system/light/dark), language, notification settings, sync controls, privacy, help, open-source notices.
- Dynamic type, TalkBack semantics, contrast, large touch targets, and reduced-motion support.
- X Smoothness: request a supported display mode; never promise forced FPS or bypass OEM/system policy.

### K. X AI (separate optional service)
- Explain assignment instructions, generate study plans, summarize user-provided course materials, quiz / flashcard generation.
- AI is opt-in, visibly indicates external processing, and never submits work automatically.
- Redact personal identifiers and do not send private class data without clear consent.
- Provider routing, quotas, budgets, and secrets belong on a backend.

## Official API mapping and limitations

| Classroom X area | Public Google API resource | Notes |
|---|---|---|
| Courses | courses | Visibility depends on user role and scopes |
| Roster | students, teachers, userProfiles | Permission-sensitive |
| Stream | announcements | Stream is represented by multiple resource types |
| Classwork | courseWork, courseWorkMaterials, topics | Not every first-party UI feature has a public API equivalent |
| Submissions | studentSubmissions | Student / teacher permissions differ |
| Rubrics | courseWork rubrics | Availability depends on feature eligibility and permissions |
| Push updates | registrations + Pub/Sub | Requires backend endpoint and Cloud Pub/Sub setup |
| Files | Drive API / Google Drive app | Classroom attachments may reference Drive resources; Drive scopes are separate |
| Calendar | Google Calendar API | Separate integration and scopes |
| Mail | Gmail / Android mail intent | Separate integration; avoid broad Gmail scopes unless necessary |

## Important limitations

Google Classroom's official mobile app includes product experiences that are not all exposed by the public Classroom API. Some capabilities require separate Google APIs, specific Workspace for Education editions, teacher/student role permissions, or Google's own first-party app. Classroom X must show a graceful hand-off or an explicit "not supported by the public API" state instead of scraping private endpoints or pretending to support the feature.

## Delivery phases

1. **Foundation** — navigation, Material 3 design tokens, state models, local demo data, empty/loading/error states.
2. **Read-only Classroom** — OAuth, courses, course details, classwork, announcements, materials, topics, submissions and grades visible to the signed-in user.
3. **Student workflows** — assignment details, turn-in / reclaim where supported, attachment hand-off, feedback, planner.
4. **Teacher workflows** — create/manage posts and assignments, grading and roster actions only after teacher-role support and explicit write scopes.
5. **Notifications and integrations** — local reminders first; Classroom push registrations, Drive and Calendar integrations later.
6. **X AI and security** — backend gateway, provider routing, privacy controls, rate limits, audit-safe diagnostics.
7. **Polish** — accessibility, localization, performance, adaptive refresh rate, Liquid Glass visual experiment.

## Definition of done for each feature

- UI states: loading, content, empty, offline/stale, permission denied, unsupported, and recoverable error.
- Unit tests for mapping / business rules.
- No hardcoded credentials or real student data.
- API calls go through repositories and are cancellable.
- Feature is tested against a Google Workspace test account and a student account where applicable.
- README documents required OAuth scopes and external setup.
