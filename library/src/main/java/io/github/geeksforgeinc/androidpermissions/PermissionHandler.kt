package io.github.geeksforgeinc.androidpermissions

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


class PermissionHandler(
    private val activity: Activity?,
    private val packageName: String,
    private val permissionOptions: PermissionOptions,
    private val onPermissionGranted: () -> Unit
) : DefaultLifecycleObserver {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var permissionDeniedList: Array<String>
    override fun onCreate(owner: LifecycleOwner) {

        activity?.let { activity ->
            permissionLauncher = (activity as ComponentActivity).activityResultRegistry.register(
                "permissionLauncher",
                ActivityResultContracts.RequestMultiplePermissions()
            ) { map ->
                permissionDeniedList = map.filter { !it.value }.keys.toTypedArray()
                if (permissionDeniedList.isEmpty()) {
                    onPermissionGranted()
                } else {
                    if (PermissionUtils.shouldShowRequestPermissionRationale(
                            activity,
                            permissionDeniedList
                        )
                    ) {
                            ShadowActivity.showRationaleWithDialog(
                                activity,
                                permissionOptions as DialogPermissionOptions,
                                packageName,
                                permissionDeniedList
                            )
                    } else {
                        if (permissionOptions.settingsEnabled()) {
                                ShadowActivity.launchSettingsWithDialog(
                                    activity,
                                    permissionOptions as DialogPermissionOptions,
                                    packageName,
                                    permissionDeniedList
                                )
                            }
                    }
                }
            }
        }
    }

    fun requestPermissions(vararg permissions: String) {
        permissionLauncher.launch(arrayOf(*permissions))
    }
}


