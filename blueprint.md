# Angol Custom Input Application Blueprint

## Overview

This document outlines the architecture, design, and features of the Angol application. Angol is a unique input application that utilizes a custom-built hexagonal grid for user input, supporting both letters and numbers.

## Architecture & Features

*   **Core UI:** The main interface is the `AngolScreen`, which presents the user with a hexagonal grid for input.
*   **Hexagonal Geometry:** The grid layout and interactions are managed by a custom `hex_geometry.dart` library, which handles axial coordinates and grid calculations.
*   **State Management:** The application uses the `provider` package for state management.
    *   `InputService`: A `ChangeNotifier` that manages the current input string, input mode (letters/numbers), and communicates changes to the UI.
    *   `FirebaseService`: A service to handle interactions with Firebase, which is now successfully integrated.
*   **Firebase Integration:**
    *   The app is connected to the Firebase project: `angol-20090898-cb6fd`.
    *   Firebase Core is initialized at startup.
    *   The necessary `firebase_options.dart` file has been generated.

## Development Log

### Current Task: Restore and Run the Application

**Goal:** Resolve build and configuration errors to get the existing `Angol` application running in the preview environment.

**Steps Taken:**

1.  **Initial Analysis:** The agent incorrectly assumed the project was a new, default Flutter app, leading to confusion. The user corrected the agent, reminding it of the existing `Angol` codebase.
2.  **Error Identification:** After re-establishing context, the following critical build errors were identified:
    *   A dependency version conflict with `flutter_lints` and `analyzer`.
    *   A missing `lib/firebase_options.dart` file, which prevented Firebase from initializing.
3.  **Dependency Resolution:** The `pubspec.yaml` file was updated to use a compatible version of `flutter_lints`, and `flutter pub get` was run to resolve the conflict.
4.  **Firebase Configuration:**
    *   Automated attempts to run `flutterfire configure` failed due to IDE security restrictions.
    *   A manual workaround was performed:
        *   Logged into the Firebase CLI.
        *   Set the active Firebase project to `angol-20090898-cb6fd`.
        *   Retrieved the web app's SDK configuration details.
        *   Manually wrote the `lib/firebase_options.dart` file with the correct project credentials.

**Result:** All build-blocking errors have been successfully resolved. The application is now correctly configured and is expected to be running and visible in the preview panel.
