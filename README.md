# Conditional Login App

An Android application demonstrating context-aware and environmental authentication methods as alternatives to traditional password entry. This app showcases various authentication mechanisms that leverage device sensors and contextual information.

## Overview

Conditional Login provides multiple authentication methods, each represented as a "lock" that can be unlocked by satisfying specific environmental or contextual conditions:

1. **Photo Verification**: Authenticate by taking a recent photo containing a specific text sequence
2. **Brightness Dance**: Authenticate by adjusting screen brightness in a specific pattern
3. **Bluetooth Authentication**: Authenticate by having a trusted Bluetooth device in the paired list
4. **SMS Verification**: Authenticate via receiving a specific message from a trusted number
5. **Light Sensor**: Authenticate by being in a low-light environment

## Authentication Methods

### Recent Photo Verification
- Takes a photo from your gallery
- Verifies the photo was taken within the last 5 minutes
- Uses ML Kit Text Recognition to verify the photo contains the sequence

### Brightness Dance Verification
- Requires adjusting screen brightness in a specific pattern:
  1. Increase brightness to maximum (≥90%)
  2. Decrease brightness to minimum (≤10%)
  3. Increase brightness back to maximum (≥90%)
- Provides haptic feedback (vibration) at each successful step

### Bluetooth Context Authentication
- Checks for a paired Bluetooth device
- Verifies if device matches a trusted device name or MAC address is in the paired list

### SMS Verification
- Checks for recent SMS messages
- Authenticates when a message with specific content is received from a trusted number
- Verifies the message was received within the last 5 minutes

### Light Sensor Verification
- Uses the device's ambient light sensor
- Authenticates when the ambient light level is below a specific threshold
- Ideal for verifying the user is in a dark environment

## Requirements

- Android device running Android 6.0 (API level 23) or higher
- Camera access for photo verification
- Bluetooth capability for Bluetooth authentication
- SMS access for message verification
- Ambient light sensor for light-level verification

## Installation

1. Clone the repository
2. Open the project in Android Studio
3. Build and run the application on your device

## Usage

1. Launch the app
2. Tap on any of the lock icons to attempt the corresponding authentication method
3. Follow the prompts or fulfill the required conditions
4. Successfully authenticated locks will change to an "unlocked" state

## Customization

You can customize the authentication parameters by modifying the constants in the `Constants.kt` file:

```kotlin
package com.example.conditionallogin.utils

class Constants {
    object BL_TARGET{
        const val TARGET_DEVICE_NAME = "Bluetooth Device"
        const val TARGET_DEVICE_ADDRESS = "62:13:4F:EF:E2:67"
        const val BLUETOOTH_PERMISSION_REQUEST_CODE = 2
    }

    object LIGHT_VERIFICATION {
        const val THRESHOLD = 10f
    }

    object RECENT_PHOTO_VERIFICATION {
        const val PASSWORD = "1234"
    }

    object SMS_VERIFICATION {
        const val TRUSTED_NUMBER = "+972123456789"
        const val EXPECTED_MESSAGE = "hello"
    }
}
```

This allows you to:
- Change the target Bluetooth device for authentication
- Adjust the light sensor threshold value
- Modify the text string for photo verification
- Update the trusted phone number and expected message content for SMS verification
