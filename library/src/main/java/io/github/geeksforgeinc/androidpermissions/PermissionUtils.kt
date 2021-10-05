package io.github.geeksforgeinc.androidpermissions

import android.app.Activity
import androidx.core.app.ActivityCompat

object PermissionUtils {
    fun shouldShowRequestPermissionRationale(
        activity: Activity?,
        deniedPermissionList: Array<String>
    ): Boolean {
        for (deniedPermission in deniedPermissionList) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, deniedPermission)) {
                return true
            }
        }
        return false
    }


}