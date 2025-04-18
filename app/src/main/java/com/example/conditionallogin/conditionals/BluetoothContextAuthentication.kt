package com.example.conditionallogin.conditionals

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.conditionallogin.MainActivity

class BluetoothContextAuthentication {

    private var onResultCallback: ((Boolean) -> Unit)? = null
    private val targetDeviceName = "Galaxy Buds Pro (E377)"  // Set your target Bluetooth device name
    private val targetDeviceAddress = "64:03:7F:ED:E3:77"  // Set your target device MAC address

    fun execute(context: Context, onResult: (Boolean) -> Unit) {
        onResultCallback = onResult

        // Check for required permissions
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isEmpty()) {
            checkBluetoothConnection(context)
        } else {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                context as MainActivity,
                missingPermissions,
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkBluetoothConnection(context: Context) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Log.d("Bluetooth", "Device doesn't support Bluetooth")
            onResultCallback?.invoke(false)
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.d("Bluetooth", "Bluetooth is not enabled")
            onResultCallback?.invoke(false)
            return
        }

        // Get paired devices
        val pairedDevices: Set<BluetoothDevice> = try {
            bluetoothAdapter.bondedDevices
        } catch (e: SecurityException) {
            Log.e("Bluetooth", "Permission denied: ${e.message}")
            onResultCallback?.invoke(false)
            return
        }

        if (pairedDevices.isEmpty()) {
            Log.d("Bluetooth", "No paired devices found")
            onResultCallback?.invoke(false)
            return
        }

        // Check if target device is connected
        val isTargetDeviceConnected = pairedDevices.any { device ->
            // We can check by name or address (address is more reliable)
            val isConnected = try {
                // Attempt to get connection state (this requires runtime permission check on newer Android versions)
                val deviceName = device.name
                val deviceAddress = device.address

                Log.d("Bluetooth", "Checking device: $deviceName ($deviceAddress)")

                // Match either by address (preferred) or by name
                (deviceAddress == targetDeviceAddress || deviceName == targetDeviceName)
            } catch (e: SecurityException) {
                Log.e("Bluetooth", "Error checking device: ${e.message}")
                false
            }
            isConnected
        }

        Log.d("Bluetooth", "Target device connected: $isTargetDeviceConnected")
        onResultCallback?.invoke(isTargetDeviceConnected)
    }

    companion object {
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 2
    }
}