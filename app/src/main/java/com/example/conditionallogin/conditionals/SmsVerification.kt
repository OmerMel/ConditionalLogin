package com.example.conditionallogin.conditionals

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.conditionallogin.MainActivity

class SmsVerification(private val context: Context) {

    private var onResultCallback: ((Boolean) -> Unit)? = null
    private val trustedNumber = "+972528957959"
    private val expectedMessage = "hello"

    fun execute(context: Context, onResult: (Boolean) -> Unit) {
        onResultCallback = onResult

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            isRecentMessageFromTrustedContact(trustedNumber, expectedMessage)
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                context as MainActivity,
                arrayOf(Manifest.permission.READ_SMS),
                1
            )
        }

        onResultCallback?.invoke(false)
    }

    private fun isRecentMessageFromTrustedContact(
        trustedNumber: String,
        expectedMessage: String,
        timeWindowMinutes: Int = 5
    ): Boolean {
        try {
            val uriSms: Uri = Telephony.Sms.Inbox.CONTENT_URI
            val projection = arrayOf("address", "body", "date")

            val cursor = context.contentResolver.query(
                uriSms,
                projection,
                null, // No selection filter in SQL
                null,
                "date DESC LIMIT 20" // Get the most recent messages
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val dateIndex = it.getColumnIndex("date")
                    val bodyIndex = it.getColumnIndex("body")
                    val addressIndex = it.getColumnIndex("address")

                    val smsTime = it.getLong(dateIndex)
                    val body = it.getString(bodyIndex)
                    val address = it.getString(addressIndex)

                    val currentTime = System.currentTimeMillis()
                    val diffMinutes = (currentTime - smsTime) / (1000 * 60)

                    Log.d("SmsVerification", "Found SMS from: $address with content: $body")
                    Log.d("SmsVerification", "Time difference: $diffMinutes minutes")
                    Log.d("SmsVerification", "${diffMinutes <= timeWindowMinutes && body == expectedMessage}")

                    if ((diffMinutes <= timeWindowMinutes) && (body == expectedMessage) && (address == trustedNumber)) {
                        onResultCallback?.invoke(true)
                    } else {
                        onResultCallback?.invoke(false)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SmsVerification", "Error checking SMS: ${e.message}")
        }

        return false
    }
}