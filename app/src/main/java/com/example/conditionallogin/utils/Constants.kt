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