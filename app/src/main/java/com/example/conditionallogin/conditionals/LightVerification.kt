package com.example.conditionallogin.conditionals

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.conditionallogin.MainActivity

class LightVerification(
    private val context: Context,
) : SensorEventListener {

    private var onResultCallback: ((Boolean) -> Unit)? = null
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private var triggered = false
    private var threshold = 10f

    fun execute(context: Context, onResult: (Boolean) -> Unit) {
        onResultCallback = onResult

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                context as MainActivity,
                arrayOf(Manifest.permission.READ_SMS),
                1
            )
        }
    }

    private fun startListening() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor != null) {
            sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.e("LightVerification", "Light sensor not available")
            onResultCallback?.invoke(false)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val lux = event.values[0]

        val result = lux <= threshold
        Log.d("LightVerification", "Lux: $lux, Threshold: $threshold, Pass: $result")
        stopListening()
        onResultCallback?.invoke(result)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}