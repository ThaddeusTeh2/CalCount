# CalCount – A Comprehensive Daily Diet Tracker

## Overview

CalCount is a feature-rich Android app built with Kotlin that allows users to log meals, track daily calorie intake, and compare against user-defined maintenance goals. The app demonstrates modern Android development concepts including Material Design, Jetpack Navigation, Room database, MVVM architecture, and reactive programming with Flow and LiveData.

## Features

### Core Functionality

- **Meal Management**: Create, edit, and delete meals with timestamps
- **Food Item Tracking**: Add individual food items to meals with calorie counts
- **Daily Calorie Summaries**: View total calories consumed per day
- **Maintenance Goal Tracking**: Set and track against personal calorie targets
- **Visual Feedback**: Dynamic card colors based on deviation from target
- **Date-based Organization**: Group meals by day with intuitive navigation

### User Interface

- **Material Design**: Modern UI with Material Components
- **Responsive Layout**: Adapts to keyboard visibility and screen sizes
- **RecyclerView Lists**: Efficient display of meals and food items
- **Navigation Component**: Seamless fragment-based navigation
- **Bottom Navigation**: Easy access to main features

### Data Management

- **Room Database**: Local SQLite database with KAPT code generation
- **Entity Relationships**: Foreign key relationships between meals and food items
- **Data Persistence**: Settings and data survive app restarts
- **Reactive Updates**: Real-time UI updates with Flow and LiveData

## Architecture

### MVVM Pattern

- **Model**: Room entities and domain models
- **View**: Fragments with ViewBinding
- **ViewModel**: Business logic and state management
- **Repository**: Data access abstraction layer

### Key Components

- **Single Activity**: MainActivity hosts all fragments
- **Navigation Graph**: Centralized navigation configuration
- **Repository Pattern**: Clean separation of data access
- **Dependency Injection**: Manual DI with Application class

## Tech Stack

### Core Technologies

- **Language**: Kotlin 2.0.21
- **Build System**: Gradle with Version Catalog
- **Min SDK**: 30 (Android 11)
- **Target SDK**: 36 (Android 14)

### UI & Navigation

- **Material Components**: 1.13.0
- **Navigation Component**: 2.9.5
- **ViewBinding**: Enabled for type-safe view access
- **ConstraintLayout**: Flexible layout system

### Architecture & Data

- **Room Database**: 2.7.2 with KAPT
- **Lifecycle Components**: ViewModel, LiveData, Fragment KTX
- **Coroutines**: Flow-based reactive programming
- **SharedPreferences**: Settings persistence

### Development Tools

- **KAPT**: Room code generation
- **Safe Args**: Type-safe navigation arguments
- **Kotlin Serialization**: Data serialization support

## Project Structure

### Application Layer

- `MainActivity` – Single-activity host with Navigation Component and keyboard handling
- `MyApp` – Application class with database initialization and repository setup

### UI Layer (`ui/`)

- `HomeFragment` – Daily calorie summaries with meal cards
- `MealDetailFragment` – Individual meal view with food items
- `AddEditMealFragment` – Create/edit meal entries
- `AddEditItemFragment` – Add/edit food items within meals
- `SettingsFragment` – Configure maintenance calorie target
- `MealViewModel` – Business logic for meal operations
- `SettingsViewModel` – Settings management logic

### UI Adapters (`ui/adapter/`)

- `DayMealAdapter` – RecyclerView adapter for daily meal lists
- `MealAdapter` – Adapter for meal cards
- `FoodAdapter` – Adapter for food item lists

### Data Layer (`data/`)

- `MealRepository` – Central data access layer with domain mapping
- `model/` – Domain models (Meal, FoodItem)
- `model/local/` – Room database components
  - `AppDatabase` – Room database configuration
  - `MealDao` – Data access object for database operations
  - `entities/` – Room entities (MealEntity, FoodItemEntity, MealsWithItems)

### Preferences (`prefs/`)

- `CaloriePrefs` – Singleton for settings management with SharedPreferences

### Resources

- **Layouts**: 9 XML layout files for fragments and list items
- **Navigation**: Navigation graph with 5 destinations
- **Strings**: Localized string resources
- **Themes**: Material Design theme configuration
- **Drawables**: Custom icons and graphics

## Database Schema

### Tables

- **meals**: Stores meal information (id, name, timestamp, totalCalories)
- **food_items**: Stores individual food items (id, mealOwnerId, name, calories)
- **Relationships**: Foreign key constraint with CASCADE delete

### Key Features

- **Auto-generated IDs**: Primary keys with auto-increment
- **Timestamp Handling**: Epoch millisecond storage with timezone conversion
- **Calorie Aggregation**: Automatic total calculation for meals
- **Data Integrity**: Foreign key constraints and indices

## Getting Started

### Prerequisites

- Android Studio (latest stable version)
- Android SDK 30+ (Android 11+)
- Java 11+ (for compilation)

### Installation

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle to install dependencies
4. Run on emulator or device (min SDK 30)

### Build Configuration

- **Gradle**: Version catalog for dependency management
- **KAPT**: Room code generation enabled
- **ProGuard**: Configured for release builds
- **ViewBinding**: Enabled for type-safe UI access

## Development Notes

### Key Implementation Details

- **Keyboard Handling**: Bottom navigation hides when keyboard is visible
- **Reactive UI**: Flow-based data streams with automatic UI updates
- **Memory Management**: Proper ViewBinding cleanup in fragments
- **Error Handling**: Comprehensive validation for user inputs
- **Performance**: Efficient RecyclerView adapters with proper view recycling

### Code Organization

- **Package Structure**: Clear separation by feature and layer
- **Naming Conventions**: Consistent Kotlin naming throughout
- **Documentation**: Inline comments for complex logic
- **Type Safety**: Extensive use of Kotlin's type system
