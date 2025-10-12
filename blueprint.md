
# Project Blueprint

## Overview

`angol` is a Flutter application designed as a hexagonal modular input system. It is integrated with Firebase for backend services, including Authentication and Firestore database.

## Style, Design, and Features

*   **Framework:** Flutter
*   **Backend:** Firebase
    *   Firebase Authentication
    *   Cloud Firestore
*   **Dependencies:**
    *   `firebase_core`
    *   `firebase_auth`
    *   `cloud_firestore`
    *   `intl`
    *   `provider`

## Development Log

### Initial Setup & Debugging

This section details the extensive debugging process required to get the application running correctly after initial setup.

**1. The Initial Problem: "Permission Denied"**

*   **Symptom:** The application would compile, but the web preview would be a blank white screen. The browser's developer console showed a "permission-denied" error when trying to connect to Firestore.
*   **Initial Action:** The active Firebase project was switched from the incorrect `angol-20090898-cb6fd` to the correct `angol-38753`.
*   **Result:** This did **not** solve the problem.

**2. Incorrect Diagnosis: Authorized Domains**

*   **Hypothesis:** The "permission-denied" error was caused by the application's domain not being on the Firebase project's "Authorized Domains" list.
*   **Action:** Attempted to add the preview domain (`*.web.app`, `*.firebaseapp.com`) to the list.
*   **Result:** This was a red herring. The core issue was not related to the authorized domains.

**3. Incorrect Diagnosis: Stale Application Process**

*   **Hypothesis:** A "stale" or "zombie" process of the old, broken application was still running, preventing the newly configured version from starting. This was supported by `Address already in use` errors.
*   **Actions:**
    *   Attempted to kill the process using `ps`, `awk`, and `kill`. These commands failed.
    *   Attempted to use `fuser` to free the port. The command was not available in the environment.
    *   Attempted to use `idx previews restart`. The command was not available.
*   **Result:** All attempts to programmatically restart the server failed, leading to significant wasted time and frustration.

**4. The Real Problem: Hardcoded Project Configuration**

*   **Correct Diagnosis (with user guidance):** The user correctly pointed out that the problem was deeper. A review of `lib/firebase_options.dart` revealed the root cause: the `projectId` was **hardcoded** to the incorrect project (`angol-20090898-cb6fd`).
*   **The Solution Path:**
    1.  **Attempt `flutterfire configure`:** The command `flutterfire configure --project=angol-38753` was run to regenerate the options file.
    2.  **Tooling Failure:** This command failed because the `flutterfire_cli` tool was out of date.
    3.  **Update Tooling:** The command `dart pub global activate flutterfire_cli` was run to update the CLI tool.
    4.  **Process Conflict (User Diagnosis):** The user brilliantly identified that multiple, conflicting `flutterfire` processes were running simultaneously, causing the command to hang.
    5.  **Kill Conflicts:** The conflicting processes were killed using a `ps | grep | kill` chain.
    6.  **Successful Configuration:** `flutterfire configure` was run one last time. It required interactive user input to select the `web` platform and confirm overwriting the existing file.
    7.  **Confirmation:** The contents of `lib/firebase_options.dart` were read and confirmed to contain the correct `projectId: 'angol-38753'`.

### Current Plan: Finalization

*   **Action:** Update Firebase dependencies in `pubspec.yaml` to the latest stable versions.
*   **Action:** Run `flutter pub get` to install the updated packages.
*   **Goal:** Run the application, which should now successfully connect to the correct Firebase project.
