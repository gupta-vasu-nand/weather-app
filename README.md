# WeatherApp — Modern Android Weather Application

WeatherApp is a modern Android application built with Jetpack Compose that provides real-time weather information using the WeatherAPI service.
The app follows clean MVVM architecture principles, supports offline storage, and delivers a polished Material You user interface.

---

## Overview

WeatherApp helps users stay updated with accurate weather information while offering a smooth and customizable user experience. Users can manage multiple cities, view detailed atmospheric conditions, and personalize units and themes according to their preferences.

---

## Features

### Real-Time Weather

* Live weather updates for any city
* Temperature, humidity, wind speed, pressure, UV index
* Air quality metrics and pollutant levels
* “Feels like” temperature and visibility data

### City Management

* Save cities as Home, Work, or Other
* Mark cities as favorites
* Set a default city
* Add custom cities manually
* Smart city search with suggestions

### Modern User Interface

* Built entirely with Jetpack Compose
* Material You design system
* Adaptive light, dark, and system themes
* Weather-based gradient backgrounds
* Smooth animations and transitions
* Shimmer loading states
* User-friendly empty and error screens

### Customization

* Temperature units: Celsius / Fahrenheit
* Wind speed units: km/h / mph
* Theme modes: Light / Dark / System default
* Notification preferences

### Offline Support

* Local caching using Room database
* Saved cities stored on device
* Preferences persisted using DataStore

---

## Application Screenshots

### Home Screen

[View Image](app/src/main/res/drawable/showcase_home.png)

### City Search

[View Image](app/src/main/res/drawable/showcase_search.png)

### Settings

[View Image](app/src/main/res/drawable/showcase_settings.png)

---

## Architecture

The application follows MVVM Clean Architecture principles:

Presentation Layer (Jetpack Compose UI)
ViewModels (State Management)
Domain Layer (Business Logic & Use Cases)
Data Layer (Repository Pattern)
Remote API + Local Database

### Architectural Highlights

* Unidirectional data flow
* Repository pattern implementation
* StateFlow with lifecycle-aware state collection
* Dependency Injection using Hilt
* Modular and scalable project structure

---

## Technology Stack

### UI

* Jetpack Compose
* Material 3
* Navigation Compose
* Coil Image Loading

### Architecture

* MVVM with Clean Architecture
* Hilt Dependency Injection
* Kotlin Coroutines and Flow

### Networking

* Retrofit2
* Kotlinx Serialization
* OkHttp Logging Interceptor

### Local Storage

* Room Database
* Kotlin Symbol Processing (KSP)
* DataStore Preferences

---

## API Integration

Weather data is provided by WeatherAPI.

API Endpoint Used:

```
/v1/current.json
```

Website:
https://www.weatherapi.com/

---

## API Key Setup

Add your API key inside the local properties file:

```
local.properties
```

```
WEATHER_API_KEY=your_api_key_here
```

Expose the key securely through BuildConfig.

---

## Installation Guide

1. Clone the repository

```
git clone https://github.com/yourusername/weatherapp.git
```

2. Open the project in Android Studio

3. Add your WeatherAPI key

4. Sync Gradle dependencies

5. Run on an emulator or physical device

---

## Project Structure

```
com.weatherapp
│
├── data
│   ├── local
│   ├── remote
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── presentation
│   ├── components
│   ├── screens
│   ├── navigation
│   └── theme
│
├── di
└── utils
```

---

## Key Highlights

* Modern Material You interface
* Offline-first architecture
* Smooth and responsive performance
* Clean and scalable codebase
* Production-ready structure

---

## License

This project is licensed under the MIT License.

---

## Developer

Built using Kotlin and modern Android development tools.
