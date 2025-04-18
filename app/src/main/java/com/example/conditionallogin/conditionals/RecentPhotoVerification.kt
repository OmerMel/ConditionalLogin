package com.example.conditionallogin.conditionals

import android.content.Context
import androidx. exifinterface. media. ExifInterface
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecentPhotoVerification(private val launcher: ActivityResultLauncher<String>) {

    private var onResultCallback: ((Boolean) -> Unit)? = null

    fun execute(context: Context, onResult: (Boolean) -> Unit) {
        this.onResultCallback = onResult
        launcher.launch("image/*")
    }

    fun handleImageResult(context: Context, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exif = inputStream?.let { ExifInterface(it) }

        val dateTime = exif?.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif?.getAttribute(ExifInterface.TAG_DATETIME)

        if (dateTime != null) {
            val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
            val photoDate = sdf.parse(dateTime)
            val currentTime = Date()
            val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTime.time - photoDate.time)

            //check if the photo taken within the last 5 minutes
            if (diffMinutes < 5) {
                checkNumberSequence(context, uri)
            } else {
                onResultCallback?.invoke(false)
            }
        } else {
            onResultCallback?.invoke(false)
        }
    }

    private fun checkNumberSequence(context: Context, uri: Uri) {
        val image = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                onResultCallback?.invoke(PASSWORD in text)
            }
            .addOnFailureListener {
                onResultCallback?.invoke(false)
            }
    }

    companion object {
        private const val PASSWORD = "1234"
    }
}