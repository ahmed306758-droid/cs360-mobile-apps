# Study Planner Launch Plan

## App Description
Study Planner is an Android assignment-management application designed for students who want one place to organize courses, assignments, due dates, priorities, notes, and optional SMS reminders. The store description will emphasize simple assignment management, persistent local storage, and user-controlled reminders.

## App Icon
The app should use a simple academic icon, such as a checklist or calendar combined with a graduation-cap symbol. The design should remain recognizable at small sizes and use the same visual style as the application.

## Android Compatibility
The project currently targets and compiles against Android API 37 (Android 17) and supports devices from API 24 (Android 7.0) and newer. Before release, the app should be tested on representative Android 7 through Android 17 emulator/device configurations. The final release should be rebuilt with a stable Android Studio/Android Gradle Plugin combination appropriate for the selected target SDK.

## Permissions
The app requests only `SEND_SMS`, because SMS is the only feature requiring a dangerous runtime permission. The user is asked for permission before SMS reminders are enabled. If permission is denied, assignment management, the database, calendar screen, and other application features continue to work.

## Monetization
The initial release will be free and will not include advertisements. This keeps the study-planning interface focused and avoids distracting users while they are managing schoolwork. A future version could offer an optional one-time premium upgrade for additional planning features.

## Testing Before Release
- Test account creation with valid and invalid input.
- Test login with correct and incorrect credentials.
- Verify user data remains after closing and reopening the application.
- Test Create, Read, Update, and Delete assignment operations.
- Test SMS permission granted and denied.
- Verify that SMS reminders are sent only when the user enables them and permission is granted.
- Test the application on multiple screen sizes and Android versions.
- Test the release build and verify that there are no crashes or layout problems.

## Distribution
The release process will include creating a signed release build, preparing screenshots and the store listing, completing the privacy/data-safety disclosures, and uploading the app through Google Play Console. The final store listing will include the app name, icon, description, screenshots, supported Android version information, permission explanation, and contact/support information.
