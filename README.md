# MYRA AI Assistant — Starter Project

This is the first buildable Android starter for MYRA. It includes:
- Kotlin + Jetpack Compose interface
- Android speech-recognition prompt (Hindi locale)
- Android Text-to-Speech reply
- Basic YouTube and browser launch commands
- GitHub Actions workflow to build a debug APK

## Build
GitHub Actions runs on pushes to `main` or manually from Actions > Build MYRA APK > Run workflow.
Download the `MYRA-debug-apk` artifact from the completed workflow run.

## Important
This is a starter, not the finished assistant. AI API integration, 3D avatar/lip sync,
screen capture, camera sessions, accessibility-based interaction, background wake word,
messaging/call workflows, and persistent memory are not implemented yet.
Android permissions and user/system confirmation screens will be required for protected actions.
Never put private API keys in source code; add them as GitHub Actions secrets when the AI integration is added.
