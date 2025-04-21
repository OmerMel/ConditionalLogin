package com.example.conditionallogin.utils

class Constants {
    object BL_TARGET{
        const val TARGET_DEVICE_NAME = "Galaxy Buds Pro (E377)"
        const val TARGET_DEVICE_ADDRESS = "64:03:7F:ED:E3:77"
        const val BLUETOOTH_PERMISSION_REQUEST_CODE = 2
    }

    object LIGHT_VERIFICATION {
        const val THRESHOLD = 10f
    }

    object RECENT_PHOTO_VERIFICATION {
        const val PASSWORD = "1234"
    }

    object SMS_VERIFICATION {
        const val TRUSTED_NUMBER = "+972528957959"
        const val EXPECTED_MESSAGE = "hello"
    }
}