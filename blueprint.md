# Project Blueprint

## Overview
This project is a Flutter application named "Angol". It integrates with Firebase for authentication and potentially other services. The application aims to provide a user interface for an interactive experience, possibly involving a keypad and hexagon-based elements.

## Style, Design, and Features
- **Theming**: Uses Material Design 3 with a dark theme (`Brightness.dark`) and a scaffold background color of `0xFF111111`.
- **Authentication**: Implements Firebase Authentication with Google Sign-In using redirect flow for web. The authentication state is managed using a `StreamBuilder` to show either a `SignInScreen` (for unauthenticated users) or a `HomePage` (for authenticated users).
- **State Management**: Utilizes `provider` for state management, specifically `ChangeNotifierProvider` for `InputService` and `AngolState`.
- **Core Screens**: 
    - `AngolScreen`: (Original main screen, now intended to be part of the authenticated flow).
    - `SignInScreen`: Provides a button to sign in with Google.
    - `HomePage`: A placeholder screen for authenticated users with a logout button.

## Current Plan and Steps for Requested Change: Fix Redirect Loops

### Problem:
The `AuthWrapper` widget in `main.dart` was calling `FirebaseAuth.instance.getRedirectResult()` in its `initState` method. This method is intended to handle the result of a federated sign-in redirect. However, calling it repeatedly on every widget initialization (which can happen on hot reloads or rebuilds) without proper state management can lead to unintended redirect loops or incorrect authentication behavior.

### Solution:
1.  **Created `lib/screens/sign_in_screen.dart`**: This screen provides a basic UI for users to initiate the sign-in process, specifically using Google Sign-In with `signInWithRedirect`.
2.  **Created `lib/screens/home_page.dart`**: This screen serves as a placeholder for authenticated users, displaying a welcome message and a logout button.
3.  **Modified `lib/main.dart`**: 
    - Updated `AuthWrapper` from a `StatefulWidget` to a `StatelessWidget`.
    - Implemented a `StreamBuilder<User?>` that listens to `FirebaseAuth.instance.authStateChanges()`.
    - The `StreamBuilder` now conditionally renders:
        - A `CircularProgressIndicator` while the connection state is `waiting`.
        - The `HomePage` if `snapshot.hasData` (user is authenticated).
        - The `SignInScreen` if the user is not authenticated.
    - Removed the `initState` method from `AuthWrapper` which contained the problematic `getRedirectResult()` call.
    - Removed the direct import and usage of `AngolScreen` from `main.dart` to ensure the authentication flow is correctly established first. `AngolScreen` should now be navigated to from `HomePage` or another authenticated route.

### Verification:
- The application should now correctly display the `SignInScreen` when no user is logged in.
- After a successful Google Sign-In redirect, the application should display the `HomePage`.
- The logout button on the `HomePage` should correctly sign out the user and return to the `SignInScreen`.
- The redirect loop issue caused by `getRedirectResult()` in `initState` should be resolved.
