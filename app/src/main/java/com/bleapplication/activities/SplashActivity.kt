package com.bleapplication.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bleapplication.modules.PermissionManager
import com.bleapplication.R
import org.jetbrains.anko.toast

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000 // 3 sec
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize a list of required permissions to request runtime
        val list = listOf<String>(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Initialize a new instance of PermissionsManager class
        managePermissions = PermissionManager(this, list, PermissionsRequestCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
          if(managePermissions.checkPermissions()) {

            Handler().postDelayed({
                // This method will be executed once the timer is over
                // Start your app main activity
                startActivity(Intent(this, IntroSliderActivity::class.java))
                // close this activity
                finish()
            }, SPLASH_TIME_OUT)}

    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)

                if (isPermissionsGranted) {
                    // Do the task now
                    // toast("Permissions granted.")
                    Handler().postDelayed({
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        startActivity(Intent(this, IntroSliderActivity::class.java))
                        // close this activity
                        finish()
                    }, SPLASH_TIME_OUT)

                } else {
                    toast("Permissions denied.")
                    finish()
                }
                return
            }
        }
    }
}
