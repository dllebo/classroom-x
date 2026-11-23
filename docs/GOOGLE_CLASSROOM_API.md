# Google Classroom API capability notes

Research basis: Google's public Classroom API guides and REST reference. This is a practical integration note, not a promise that every Google Classroom app feature is available to third-party clients.

## Resource families to evaluate

- Courses, course aliases, invitations, students, teachers, user profiles.
- CourseWork, CourseWorkMaterials, announcements, topics.
- StudentSubmissions and rubrics.
- Add-on attachments and add-on context.
- Registrations for change notifications (Google Cloud Pub/Sub delivery).

## OAuth and permissions

Use the narrowest scopes possible. Separate student read flows from teacher write flows. Google Workspace for Education feature eligibility and administrator policies can restrict access even when an endpoint exists. OAuth verification may be required for sensitive/restricted scopes and public distribution.

## Separate integrations

- Drive API: file metadata and Drive content access; Classroom attachment references do not automatically grant all Drive permissions.
- Google Calendar API: calendar events and synchronization.
- Gmail: avoid direct Gmail API integration unless a clearly justified feature needs it; use Android intents for simple email hand-off.

## API-first rule

Never reverse engineer private mobile endpoints or scrape authenticated web pages as a substitute for supported APIs. If an official API does not expose a first-party feature, label it unsupported or use a documented external hand-off.
