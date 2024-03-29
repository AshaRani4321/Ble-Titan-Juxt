package com.bleapplication.modules

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(val activity: Activity, val list: List<String>, val code: Int) {

    // Check permissions at runtime
    fun checkPermissions(): Boolean {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
           return showAlert()
        } else {
            //activity.toast("Permissions already granted.")
            return true
        }
    }

    // Check permissions status
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0;
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }

    // Show alert dialog to request permissions
    private fun showAlert(): Boolean {
        val builder = AlertDialog.Builder(activity)
        var permissionGranted: Boolean = false
        builder.setTitle("Need permission(s)")
        builder.setMessage("Some permissions are required to do the task.")
        builder.setPositiveButton("OK", { dialog, which ->
            requestPermissions()
            permissionGranted = true
        })
        builder.setNeutralButton("Cancel", { dialog, which -> activity.finish() })
        val dialog = builder.create()
        dialog.show()
        return permissionGranted
    }


    // Request the permissions at run time
    private fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an explanation asynchronously
           // activity.toast("Should show an explanation.")
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        } else {
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        }
    }


    // Process permissions result
    fun processPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}