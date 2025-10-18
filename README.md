# Terra 🌍

A modern Android application that showcases countries from around the world with detailed information and UI built with Jetpack Compose.

## 📱 Features

- **Countries List**: Browse a list of countries with flags and basic information
- **Country Details**: View detailed information about each country

## 🏗️ Architecture

Terra follows **MVVM (Model-View-ViewModel)** architecture with clean separation of concerns and modular design.

### Architecture Components

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │     Domain      │    │      Data       │
│   (UI Layer)    │    │   (Business)    │    │   (Repository)  │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ • ViewModels    │    │ • Use Cases     │    │ • Repositories  │
│ • Composables   │    │ • Models        │    │ • Data Sources  │
│ • Navigation    │    │ • Interfaces    │    │ • Mappers       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Key Architectural Principles

- **Single Responsibility**: Each module has a clear, focused purpose
- **Dependency Inversion**: High-level modules don't depend on low-level modules
- **Clean Architecture**: Clear separation between presentation, domain, and data layers
- **Modular Design**: Feature-based modules for better maintainability

## 📦 Project Structure

The project is organized into **feature modules** and **core modules**:

```
Terra/
├── app/                          # Main application module
├── feature/                      # Feature modules
│   ├── home/                     # Countries list feature
│   ├── details/                  # Country details feature
│   └── welcome/                  # Welcome/onboarding feature
├── core/                         # Core modules
│   ├── data/                     # Data layer
│   │   └── country/              # Country data implementation
│   ├── domain/                   # Domain layer
│   │   ├── common/               # Shared domain models
│   │   └── country/              # Country domain models
│   ├── network/                  # Network layer
│   └── navigation/               # Navigation utilities
└── gradle/                       # Gradle configuration
```

- [REST Countries API](https://restcountries.com/) for country data

