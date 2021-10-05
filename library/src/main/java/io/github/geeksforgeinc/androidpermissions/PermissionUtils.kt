package io.github.geeksforgeinc.androidpermissions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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

}