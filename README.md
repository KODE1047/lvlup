# Lvl Up

Lvl Up is an Android application developed as a university project for educational purposes only.

## Description

This project is an Android app built with modern Android development technologies. It demonstrates the use of various Android components and libraries in a practical application.

## Features

- Create, edit, and delete tasks with ease
- Set custom date and time for tasks with alarm notifications (implemented using Android AlarmManager and NotificationManager)
- Priority levels (High, Medium, Low) to organize your workload
- Task overview dashboard with basic analytics (task count, completion rate)
- Two home screen widgets: Quick task creation and daily task summary (implemented using Glance)
- Data persistence using Room database
- App widget support with Glance
- Material Design 3 components for consistent UI
- Hilt dependency injection for DI implementation details
- KSP (Kotlin Symbol Processing) for annotation processing

## Technologies Used

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern toolkit for building native UI
- **Hilt**: Dependency injection library (implementation details in `com.example.Lvl Up.di` package)
- **Room**: Database library for local data storage
- **Navigation Component**: For handling navigation between composables
- **Material Design 3**: For modern, consistent UI components
- **Glance**: For building app widgets (implementation in `res/xml` directory)
- **KSP**: Kotlin Symbol Processing for annotation processing (build-time implementation)

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- JDK 8 or higher
- Android SDK

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/KODE1047/Lvl Up.git
   ```

2. Open the project in Android Studio

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run the app:
   ```bash
   ./gradlew installDebug
   ```
   or use the Run button in Android Studio

## Project Structure

The project follows the standard Android project structure with a single `app` module. Key directories include:

- `app/src/main/java`: Contains the Kotlin source code organized by features
  - `com.example.Lvl Up.ui`: Jetpack Compose UI components and screens
  - `com.example.Lvl Up.data`: Room database entities and DAOs
  - `com.example.Lvl Up.repository`: Data repository layer (single source of truth)
  - `com.example.Lvl Up.viewmodel`: ViewModel classes for UI state management
  - `com.example.Lvl Up.di`: Hilt dependency injection modules
  - `com.example.Lvl Up.receiver`: AlarmReceiver for task notifications
  - `com.example.Lvl Up.service`: Background services for alarms

- `app/src/main/res`: Contains resources organized by type
  - `drawable`: App icons and vector graphics
  - `layout`: XML layouts for custom views
  - `values`: Colors, strings, and themes
  - `xml`: App widget configuration files
  - `mipmap-anydpi`: Launcher icons

- `app/src/main/AndroidManifest.xml`: Application manifest file with activity declarations and permissions

## Widgets

The application includes two home screen widgets to enhance productivity:

1. **Completion Rate Widget**
   - Visual representation of task completion rate
   - Color-coded indicator (green/red gradient based on completion percentage)
   - Simple interface with title and priority input for new tasks
   - Directly adds tasks to your to-do list

2. **Full Task Overview Widget**
   - Visual progress indicator for completed tasks
   - Total task counter with total, complete and pending breakdown
   - Tap to view full task list in the app

## Additional Features

- **Dark/Light Mode**: System-wide theme toggle
- **Data Persistence**: Tasks saved securely using Room database
- **Notification System**: Reminders and completion notifications
- **Analytics Dashboard**: Visual representation of task completion rates


## Limitations

As an educational project, this application has some limitations:

- No user authentication system
- Data is stored locally only (no cloud sync)
- Limited third-party integrations
- Basic search functionality (no advanced filters or sorting)
- Alarm system uses standard Android notifications (no advanced scheduling)
- Widgets have basic functionality (no advanced customization or configuration)

These limitations are intentional to focus on core Android development concepts and best practices.

## Learning Objectives

This project was designed to demonstrate and practice the following Android development concepts:

- **Modern Android Architecture**: Implementation of a single-activity architecture with multiple composables
- **Jetpack Compose**: Hands-on experience with declarative UI development
- **State Management**: Practical use of Compose state and ViewModel
- **Dependency Injection**: Implementation of Hilt for DI
- **Local Data Storage**: Use of Room database with DAO patterns
- **Alarm System**: Implementation of Android's AlarmManager
- **App Widgets**: Creation of home screen widgets using Glance
- **Material Design 3**: Application of modern design principles
- **Async Programming**: Handling background tasks and notifications
- **Testing Basics**: Introduction to unit and instrumentation tests

The project intentionally includes these concepts to provide a comprehensive learning experience for Android developers.

## Future Improvements

As an educational project, here are some potential improvements that could be made:

**For Students:**
- Add cloud synchronization using Firebase or similar services
- Implement advanced search with filters and sorting options
- Create a backup/restore feature for task data
- Add task categories or tags
- Implement recurring tasks
- Add calendar integration
- Create a widget customization system
- Complete full analytics dashboard implementation
- Implement subtask management system

**For Educators:**
- Add code quality requirements (e.g., specific test coverage)
- Set performance optimization goals (e.g., improve app startup time)
- Include security best practices (e.g., data encryption)
- Challenge students to optimize for different screen sizes and orientations
- Add accessibility improvements
- Implement internationalization support

These improvements can serve as advanced assignments or capstone project extensions.

## Note

This project is for educational purposes only and is not intended for production use.

## Note for Educators

This project is designed as an educational tool for teaching Android development. Here are some suggestions for using it in an educational setting:

- **Project-Based Learning**: Use the app as a foundation for a semester-long project
- **Code Review Exercises**: Analyze specific components (e.g., the Hilt DI implementation or Glance widgets)
- **Feature Expansion Assignments**: Challenge students to add new features like cloud sync or advanced search
- **Code Refactoring Practice**: Identify areas for improvement in the codebase
- **Testing Exercises**: Expand the test coverage for untested components
- **UI/UX Design Challenges**: Redesign specific screens or improve the user experience
- **Build Process Analysis**: Study the KSP implementation in the build system

The project intentionally includes a range of Android concepts to provide multiple avenues for learning and expansion.

## Contributing

As this is a university project, contributions are not expected. However, feel free to fork the repository and use it for your own learning purposes.

## License

This project is for educational purposes only and does not have a specific license.

### Key Features Guide

**1. Task Management**
- To create a task: Tap the + button in the app
- To edit a task: Long-press on the task card
- To delete a task: Swipe left on the task card
- To mark complete: Tap the checkbox next to the task

**2. Setting Alarms**
- When creating/editing a task, tap the clock icon
- Select date and time for your reminder
- The app will show a notification at the scheduled time

**3. Priority Levels**
- When creating a task, select priority level (High, Medium, Low)
- Tasks are color-coded by priority:
  - Red for High priority
  - Yellow for Medium priority
  - Green for Low priority

**4. Widgets**
- Add widgets from your home screen's widget picker
- The Quick Task widget allows creating tasks without opening the app
- The Daily Summary widget shows today's tasks and progress

**5. Task Analytics**
- Access the analytics dashboard from the app drawer
- View statistics on task completion rates
- See breakdown of tasks by priority
- Track your productivity over time

## Resources

The following resources were used in the development of this educational project:

- [Android Developer Documentation](https://developer.android.com/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Persistence Library Guide](https://developer.android.com/training/data-storage/room)
- [Glance App Widgets Documentation](https://developer.android.com/jetpack/compose/appwidgets)
- [Material Design 3 Guidelines](https://m3.material.io/)
- [Android Studio Tutorials](https://developer.android.com/studio/intro)

These resources are excellent starting points for students and developers looking to expand their Android development knowledge.
