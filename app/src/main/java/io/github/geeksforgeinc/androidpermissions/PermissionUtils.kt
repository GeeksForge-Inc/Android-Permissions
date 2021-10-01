package io.github.geeksforgeinc.androidpermissions

import android.os.Build
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.lang.IllegalArgumentException
import java.util.ArrayList

object PermissionUtils {
     val isRuntimePermissionsRequired: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    // Use this method when calling from activity
    fun arePermissionsGranted(
        context: Activity?,
        requestCode: Int,
         permissions: Array<String>
    ): Boolean {
        return if (context != null) {
            val permissionNeededList: MutableList<String> = ArrayList()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionNeededList.add(permission)
                }
            }
            if (permissionNeededList.isNotEmpty()) {
                ActivityCompat
                    .requestPermissions(context, permissionNeededList.toTypedArray(), requestCode)
                return false
            }
            true
        } else {
            throw IllegalArgumentException("Context and permissions cannot be null")
        }
    }


    fun handlePermissionGrantResults(
        permissionConsentListener: PermissionConsentListener,
        activity: Activity?, permissions: Array<out String>, grantResults: IntArray
    ) {
        val deniedPermissionList: MutableList<String> = ArrayList()
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissionList.add(permissions[i])
            }
        }
        // All the permissions are granted, if this list is empty
        if (deniedPermissionList.isEmpty()) {
            permissionConsentListener.onPermissionGranted()
        } else {
            if (shouldShowRequestPermissionRationale(activity, deniedPermissionList)) {
                permissionConsentListener.onPermissionPreviouslyDenied()
            } else {
                permissionConsentListener.onPermissionDisabledPermanently()
            }
        }
    }

    fun requestRuntimePermissions(
        listener: PermissionConsentListener,
        activity: Activity?,
        requestCode: Int,
        permissions: Array<String>
    ) {
        if (isRuntimePermissionsRequired) {
            if (arePermissionsGranted(activity, requestCode, permissions)) {
                listener.onPermissionGranted()
            }
        } else {
            listener.onPermissionGranted()
        }
    }

    private fun shouldShowRequestPermissionRationale(
        activity: Activity?,
        deniedPermissionList: List<String>
    ): Boolean {
        for (deniedPermission in deniedPermissionList) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, deniedPermission)) {
                return true
            }
        }
        return false
    }

    fun launchIntentToPermissionSettings(context: Context): Boolean {
        val settingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri
                .fromParts("package", context.packageName, null)
        )
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(settingsIntent)
        return true
    }

    fun showDialog(
        context: Context?, title: String?, message: String?, positiveLabel: String?,
        negativeLabel: String?, positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
            .setTitle(title)
            .setCancelable(false)
            .setMessage(message)
            .setPositiveButton(positiveLabel) { dialog, which ->
                dialog.dismiss()
                positiveAction()
            }
            .setNegativeButton(negativeLabel){ dialog, which ->
                dialog.dismiss()
                negativeAction()
            }
        alertDialogBuilder.show()
    }

    interface PermissionConsentListener {
        //Callback on permission denied
        fun onPermissionPreviouslyDenied()

        // Callback on permission "Don't ask again" checked and denied
        fun onPermissionDisabledPermanently()

        // Callback on permission granted
        fun onPermissionGranted()
    }
}