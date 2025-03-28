# Word of the Day RAE

A simple Android application that delivers daily notifications containing the Word of the Day from the Royal Spanish Academy (Real Academia Española - RAE) dictionary website.

![imagen](https://github.com/user-attachments/assets/e1165075-5846-4c05-8cd9-cf7ac93ff205)

## Features

- 📱 Daily notifications at 12:00 AM featuring the "Word of the Day" from RAE.
- 🌐 Direct access to the complete definition on the RAE website
- 🔄 Automatic scheduling that persists after device reboots
- 🔍 Special handling for homonymous words

## Technical Details

### Components

- **BootReceiver**: Ensures notifications resume after device reboot
- **HtmlParser**: Extracts word definitions from HTML content
- **HttpClient**: Handles API communication with the RAE website server
- **MainActivity**: Manages permissions workflow and app initialization
- **NotificationService**: Creates and displays daily word notifications
- **SetAlarm**: Schedules the daily notifications

### Architecture

- Built with Kotlin
- Uses Jetpack Compose for UI elements
- Implements coroutines for asynchronous operations
- Follows modern Android development practices

### Permissions

> [!IMPORTANT]
> The application requires notification permissions to function properly. Be sure of permissions are properly granted.

## How It Works

1. The app schedules a daily alarm for 10:00 AM
2. When triggered, it connects to the RAE API to fetch the word of the day
3. It parses the word and its definition from the HTML response
4. A notification is created with the parsed content
5. Tapping the notification opens the complete definition on the RAE website

## Development

This project demonstrates several Android development concepts:

- Broadcast Receivers for system events
- Alarm scheduling with AlarmManager
- Network requests and HTML parsing
- Notification channel creation and management
- Permission handling
- Coroutines for background tasks

## Getting Started

> [!WARNING]
> This application is still in development and no official release has been published yet.

To use this application:

1. Install the app
2. Grant notification permissions when prompted
3. The app will automatically set up daily notifications

## Credits

This app utilizes the Royal Spanish Academy's "Word of the Day" service. All definitions are property of the Real Academia Española.
