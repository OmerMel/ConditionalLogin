package com.example.conditionallogin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.conditionallogin.conditionals.BrightnessDanceVerification
import com.example.conditionallogin.conditionals.RecentPhotoVerification
import com.example.conditionallogin.conditionals.BluetoothContextAuthentication
import com.example.conditionallogin.databinding.ActivityMainBinding
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.example.conditionallogin.conditionals.LightVerification
import com.example.conditionallogin.conditionals.SmsVerification

class MainActivity : AppCompatActivity() {

    private lateinit var recentPhotoConditional: RecentPhotoVerification
    private lateinit var brightnessDance: BrightnessDanceVerification
    private lateinit var bluetoothAuth: BluetoothContextAuthentication
    private lateinit var smsAuth: SmsVerification
    private lateinit var lightVerifier: LightVerification

    private lateinit var binding: ActivityMainBinding
    private var isLockImageUnlocked = false
    private var isLockBrightnessDanceUnlocked = false
    private var isLockBluetoothUnlocked = false
    private var isLockSmsUnlocked = false
    private var isLockLightUnlocked = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            recentPhotoConditional.handleImageResult(this, it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recentPhotoConditional = RecentPhotoVerification(pickImageLauncher)
        brightnessDance = BrightnessDanceVerification()
        bluetoothAuth = BluetoothContextAuthentication()
        smsAuth = SmsVerification(this)
        lightVerifier = LightVerification(this)

        initListeners()
    }

    private fun initListeners() {
        binding.lockImage.setOnClickListener {
            if (!isLockImageUnlocked) {
                recentPhotoConditional.execute(this) { success ->
                    if (success) {
                        isLockImageUnlocked = true
                        Toast.makeText(this, "Photo verified successfully!", Toast.LENGTH_SHORT).show()
                        binding.lockImage.setImageResource(R.drawable.ic_lock_open)
                    } else {
                        Toast.makeText(this, "Photo verification failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "This lock is already unlocked!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lockBrightnessDance.setOnClickListener {
            if (!isLockBrightnessDanceUnlocked) {
                vibrateShort(this)

                brightnessDance.execute(this) { success ->
                    if (success) {
                        isLockBrightnessDanceUnlocked = true
                        Toast.makeText(this, "Brightness Dance Passed!", Toast.LENGTH_SHORT).show()
                        binding.lockBrightnessDance.setImageResource(R.drawable.ic_lock_open)
                    } else {
                        Toast.makeText(this, "Try the brightness dance again!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this, "This lock is already unlocked!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lockBluetooth.setOnClickListener {
            if(!isLockBluetoothUnlocked) {
                vibrateShort(this)

                bluetoothAuth.execute(this) { success ->
                    if (success) {
                        isLockBluetoothUnlocked = true
                        Toast.makeText(
                            this,
                            "authentication passed! correct Bluetooth network.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.lockBluetooth.setImageResource(R.drawable.ic_lock_open)
                    } else {
                        Toast.makeText(
                            this,
                            "Authentication failed, wrong Bluetooth network.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "This lock is already unlocked!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lockSms.setOnClickListener {
            if(!isLockSmsUnlocked) {
                vibrateShort(this)

                smsAuth.execute(this) { success ->
                    if (success) {
                        isLockSmsUnlocked = true
                        Toast.makeText(
                            this,
                            "Verified via SMS!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.lockSms.setImageResource(R.drawable.ic_lock_open)
                    } else {
                        Toast.makeText(
                            this,
                            "SMS verification failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "This lock is already unlocked!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lockLight.setOnClickListener {
            if(!isLockLightUnlocked) {
                vibrateShort(this)

                lightVerifier.execute(this) { success ->
                    if (success) {
                        isLockLightUnlocked = true
                        Toast.makeText(
                            this,
                            "Light level OK!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.lockLight.setImageResource(R.drawable.ic_lock_open)
                    } else {
                        Toast.makeText(
                            this,
                            "Too much light detected.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "This lock is already unlocked!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            Log.d("LoginButton", "$isLockImageUnlocked, $isLockBrightnessDanceUnlocked, $isLockBluetoothUnlocked, $isLockSmsUnlocked, $isLockLightUnlocked")
            if (isLockImageUnlocked && isLockBrightnessDanceUnlocked && isLockBluetoothUnlocked && isLockSmsUnlocked && isLockLightUnlocked) {
                val intent = Intent(this, SuccessActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please unlock all locks before logging in.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun vibrateShort(context: Context) {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }

}
