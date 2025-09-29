# CalCount – A Simple Daily Diet Tracker  

## Overview  
CalCount is a lightweight Android app built with Kotlin that allows users to log meals, track daily calorie intake, and compare against a user-defined maintenance goal. The app demonstrates fundamental Android development concepts, including UI design with Material components, navigation with Jetpack, and data persistence using Room.  

## Features  
- CRUD operations for food entries (name, calories, timestamp)  
- Group meals by day with total daily calories  
- User-defined maintenance calorie target  
- Dynamic card color feedback based on deviation from target  
- RecyclerView for meal lists  
- Room Database with KAPT  
- ViewModel + LiveData for clean architecture  
- DataStore for settings persistence  
- Material Design UI with Navigation Component  

## Tech Stack  
- **Language:** Kotlin  
- **UI:** Material Components, RecyclerView, ConstraintLayout  
- **Architecture:** MVVM with ViewModel + Repository  
- **Persistence:** Room (KAPT), DataStore  
- **Navigation:** Android Jetpack Navigation Component  

## Project Structure  
- `MainActivity` – Single-activity host with Navigation Component  
- `HomeFragment` – Shows daily calorie summaries  
- `MealListFragment` – Lists meals for a specific day  
- `AddEditMealFragment` – Add or edit a meal entry  
- `SettingsFragment` – Set and persist maintenance calories  

## Getting Started  
1. Clone the repository  
2. Open in Android Studio (latest stable version)  
3. Sync Gradle to install dependencies  
4. Run on emulator or device (min SDK 30)  
