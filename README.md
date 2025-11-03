# Pexels Android App

A simple Pinterest‑like Android app built on top of the Pexels API. Users can browse curated photos, search, view details, and save bookmarks. Implemented with Jetpack Compose, a single-activity architecture, and modern Android libraries.

## Features
- Splash screen that waits for initial data
- Home (Photos) screen with search and featured/curated lists
- Photo details screen with author, zoom, download, and bookmark
- Bookmarks screen backed by local database
- Pagination (30 items per page) using Paging
- Caching of popular data and image loading placeholders
- Network error handling with retry and toasts
- Bottom navigation with active state and simple animations
- Dark theme following system setting

Note: The exact design aims to follow a Figma spec where applicable.

## Tech Stack
- Language: Kotlin
- UI: Jetpack Compose, Material 3, Compose Navigation
- Architecture: Single Activity, modular Clean Architecture (MVVM)
- DI: Hilt
- Networking: Ktor client + Kotlinx Serialization
- Concurrency: Coroutines + Flow
- Persistence: Room, DataStore (encrypted key storage)
- Paging: Paging 3 (runtime + compose)
- Images: Coil with placeholders and shimmer

## Modules
- `app`: Application entry, navigation, theming
- `feature_splash`: Splash experience and bootstrapping
- `feature_photos`: Home and details flows (lists, search, detail)
- `feature_bookmarks`: Saved items list and related UI
- `core_data`: Repositories, Room, Ktor API, DI modules
- `core_domain`: Domain layer models and use cases
- `core_ui`: Shared Compose UI components and theme
- `common_resources`: Shared resources (colors, drawables, etc.)

## Requirements
- Android Studio Ladybug or newer
- JDK 21 (project targets Java 21)
- Min SDK 24

## Setup
1. Get a Pexels API key from their developer portal.
2. In the project root, open `local.properties` and add:
   ```
   API_KEY=YOUR_PEXELS_API_KEY
   ```
   The app reads this value into `BuildConfig.API_KEY` (in `core_data`). The key is securely stored using encrypted `DataStore` on first run.
3. Sync Gradle and build the project.

## Run
- Select the `app` run configuration and press Run. The Splash screen will appear first, then the Home screen.

## Key Behaviors (implemented)
- Search bar with clear icon and hint behavior
- Featured/curated loading with horizontal/vertical lists
- Placeholders and shimmer while images load
- Offline and network error feedback with retry
- Details screen fetches from network or local DB (when opened from bookmarks)
- Bookmark add/remove with UI state update

## Notes
- Base API URL: `https://api.pexels.com/`
- Authorization header is sent per request with the API key
- Popular content and queries are cached; paging loads in 30‑item pages

