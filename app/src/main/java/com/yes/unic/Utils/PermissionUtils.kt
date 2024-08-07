package com.yes.unic.Utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtils {

     

    companion object {
        fun checkCameraPermission(context: Activity) {
                 if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request camera permission
                 } else {
//                    getCameraPicture()
                }
        }
    }
}