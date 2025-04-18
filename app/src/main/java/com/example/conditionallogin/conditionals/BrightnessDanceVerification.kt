package com.example.conditionallogin.conditionals

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log

class BrightnessDanceVerification {
    private var step = 0
    private var onResultCallback: ((Boolean) -> Unit)? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    fun execute(context: Context, onResult: (Boolean) -> Unit) {
        onResultCallback = onResult
        step = 0

        // Start listening for brightness changes
        // This should be triggered manually, or via a repeating check mechanism
        startCheckingBrightness(context)
    }

    private fun startCheckingBrightness(context: Context) {
        handler = Handler(Looper.getMainLooper())

        runnable = object : Runnable {
            override fun run() {
                checkBrightness(context)
                handler?.postDelayed(this, 1000) // Check every 1 second
            }
        }

        // Start the periodic check
        runnable?.let { handler?.post(it) }
    }

    fun stopChecking() {
        handler?.removeCallbacks(runnable!!) // Stop the periodic checking
        handler = null
        runnable = null
    }

    fun checkBrightness(context: Context) {
        val brightness = getCurrentBrightness(context)

        Log.d("Brightness", "Current brightness: $brightness")
        when (step) {
            0 -> if (brightness >= 0.9f) {
                vibrateShort(context)
                step++
            }
            1 -> if (brightness <= 0.1f) {
                vibrateShort(context)
                step++
            }
            2 -> if (brightness >= 0.9f) {
                vibrateShort(context)
                step++
                onResultCallback?.invoke(true) // Success!
                stopChecking()
            }
        }
    }

    private fun getCurrentBrightness(context: Context): Float {
        return try {
            val brightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            brightness / 255f
        } catch (e: Settings.SettingNotFoundException) {
            0f
        }
    }

    private fun vibrateShort(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}