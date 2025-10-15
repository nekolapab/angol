# Angol: Project Blueprint

## 1. Overview

**Purpose:** Angol is a Flutter application designed as a hexagonal modular input system. The goal is to create a unique and intuitive way for users to input text or commands through a dynamic, grid-based interface.

**Core Capabilities:**
-   **Hexagonal Input:** A custom-built UI featuring a grid of interactive hexagonal tiles.
-   **Modular System:** The input logic is designed to be modular, allowing for potential customization and expansion.
-   **Firebase Integration:** User data, inputs, and configurations are stored and synced using Firebase Auth and Cloud Firestore.
-   **State Management:** The app uses the `provider` package to manage state efficiently between the input service and the UI.
-   **Cross-Platform:** The application is built with Flutter and supports web as its primary initial platform.

---

## 2. Implemented Features & Project State

This section documents the project's structure and features as they have been implemented so far.

### **Style & Design:**
-   **Theme:** The application uses a `ThemeData` with `brightness: Brightness.dark` and a `primarySwatch` of `Colors.blue`.
-   **Layout:** The main screen is `AngolScreen`, which serves as the canvas for the input system.

### **Application Architecture:**
-   **Entry Point:** `lib/main.dart` initializes Firebase and sets up the root `MyApp` widget.
-   **State Management:**
    -   `ChangeNotifierProvider` is used for `InputService`.
    -   `Provider` is used for `FirebaseService`.
-   **Services:**
    -   `InputService (`services/input\_service.dart`): A `ChangeNotifier` responsible for managing the state of the user's input.
    -   `FirebaseService (`services/firebase\_service.dart`): A service class to handle all interactions with Firebase backends (Authentication and Firestore).
-   **Screens:**
    -   `AngolScreen` (`screens/angol_screen.dart`): The primary user-facing screen that will contain the hexagonal UI.

### **Dependencies:**
-   **`firebase_core`**: For initializing the Firebase app.
-   **`firebase_auth`**: For user authentication.
-   **`cloud_firestore`**: For database operations.
-   **`provider`**: For state management.
-   **`intl`**: For internationalization and formatting (though not yet used).

### **Version Control & Build:**
-   **Git:** The project is under Git version control. The repository has been cleaned, with all work consolidated onto the `main` branch.
-   **Web:** The project is configured for web deployment. The unnecessary `linux` directory has been removed.

---

## 3. Plan for Current Request: Initial Setup

**Request:** "Let's start by creating a new Flutter application and setting up a Git repository for it."

**Status: Completed**

The following steps have been successfully executed:
1.  **Application Created:** A new Flutter project named "angol" was generated.
2.  **Initial Dependencies Added:** `firebase_core`, `firebase_auth`, `cloud_firestore`, and `provider` were added to `pubspec.yaml`.
3.  **Project Structure Created:** Basic services (`InputService`, `FirebaseService`) and a main screen (`AngolScreen`) were created to establish the architecture.
4.  **Firebase Initialized:** The `main.dart` file was updated to initialize Firebase before running the app.
5.  **Git Repository Initialized:** A Git repository was created and linked to the remote on GitHub.
6.  **Repository Cleanup:** Branching issues were resolved, with all work merged into the `main` branch and the extraneous `master` branch deleted.
7.  **Build Configuration:** The `web/index.html` was configured for proper server interaction, and the unused `linux` build directory was removed.
