package io.github.geeksforgeinc.androidpermissions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class ShadowActivity : AppCompatActivity(), PermissionDialogFragment.DialogClickListener {

    companion object {
        private const val PERMISSION_REQUEST_TYPE_BUNDLE_KEY = "permission_request_type_bundle_key"
        private const val PACKAGE_NAME_BUNDLE_KEY = "package_name_bundle_key"
        private const val DIALOG_PERMISSION_OPTIONS_BUNDLE_KEY =
            "dialog_permission_options_bundle_key"
        private const val PERMISSIONS_BUNDLE_KEY = "permissions_bundle_key"
        private const val PERMISSIONS_DIALOG_TAG = "permissions_dialog_tag"

        @JvmStatic
        fun launchSettingsWithDialog(
            context: Context,
            permissionOptions: DialogPermissionOptions,
            packageName: String,
            permissions: Array<String>
        ) =
            Intent(context, ShadowActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(PERMISSION_REQUEST_TYPE_BUNDLE_KEY, Constants.SETTINGS)
                putExtra(PACKAGE_NAME_BUNDLE_KEY, packageName)
                putExtra(
                    DIALOG_PERMISSION_OPTIONS_BUNDLE_KEY,
                    permissionOptions
                )
                putExtra(PERMISSIONS_BUNDLE_KEY, permissions)
                context.startActivity(this)
            }

        @JvmStatic
        fun showRationaleWithDialog(
            context: Context,
            permissionOptions: DialogPermissionOptions,
            packageName: String,
            permissions: Array<String>
        ) =
            Intent(context, ShadowActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(PERMISSION_REQUEST_TYPE_BUNDLE_KEY, Constants.RATIONALE)
                putExtra(PACKAGE_NAME_BUNDLE_KEY, packageName)
                putExtra(PERMISSIONS_BUNDLE_KEY, permissions)
                putExtra(
                    DIALOG_PERMISSION_OPTIONS_BUNDLE_KEY,
                    permissionOptions
                )
                context.startActivity(this)
            }
    }

    private var permissions: Array<String>? = null
    private var dialogPermissionsOptions: DialogPermissionOptions? = null
    private var applicationPackageName: String? = null

    private val settingsLauncher = registerForActivityResult(SettingsActivityResult()) {
        permissionsLauncher.launch(permissions)
    }
    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            val permissionDeniedList = map.filter { !it.value }.keys.toTypedArray()
            if (permissionDeniedList.isEmpty()) {
                finish()
            } else {
                if (PermissionUtils.shouldShowRequestPermissionRationale(
                        this,
                        permissionDeniedList
                    )
                ) {
                    ifNotNull(
                        applicationPackageName,
                        dialogPermissionsOptions,
                        permissions
                    ) { packageName, options, perms ->
                        showRationaleWithDialog(
                            this, options, packageName, perms
                        )
                    }
                } else {
                    if (dialogPermissionsOptions?.settingsEnabled() == true) {
                        ifNotNull(
                            applicationPackageName,
                            dialogPermissionsOptions,
                            permissions
                        ) { packageName, options, perms ->
                            launchSettingsWithDialog(
                                this, options, packageName, perms
                            )
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }


    private fun handleIntent(intent: Intent) {
        if (intent.hasExtra(PERMISSION_REQUEST_TYPE_BUNDLE_KEY) && intent.hasExtra(
                PACKAGE_NAME_BUNDLE_KEY
            ) && intent.hasExtra(PERMISSIONS_BUNDLE_KEY)
        ) {
            applicationPackageName = intent.getStringExtra(PACKAGE_NAME_BUNDLE_KEY)
            permissions = intent.getStringArrayExtra(PERMISSIONS_BUNDLE_KEY)
            val permissionRequestType =
                intent.getIntExtra(PERMISSION_REQUEST_TYPE_BUNDLE_KEY, Constants.RATIONALE)
            if (Constants.SETTINGS == permissionRequestType) {
                if (intent.hasExtra(DIALOG_PERMISSION_OPTIONS_BUNDLE_KEY)) {
                    dialogPermissionsOptions =
                        intent.getParcelableExtra(DIALOG_PERMISSION_OPTIONS_BUNDLE_KEY)
                    ifNotNull(
                        applicationPackageName,
                        dialogPermissionsOptions,
                        permissions
                    ) { _, permissionOptions, _ ->
                        permissionOptions.settingDialogData?.let {
                            PermissionDialogFragment.showSettingsDialog(it).apply {
                                show(supportFragmentManager, PERMISSIONS_DIALOG_TAG)
                            }
                        }
                    }
                }
            } else if (Constants.RATIONALE == permissionRequestType) {
                if (intent.hasExtra(DIALOG_PERMISSION_OPTIONS_BUNDLE_KEY)) {
                    dialogPermissionsOptions =
                        intent.getParcelableExtra(DIALOG_PERMISSION_OPTIONS_BUNDLE_KEY)
                    ifNotNull(
                        applicationPackageName,
                        dialogPermissionsOptions,
                        permissions
                    ) { _, permissionOptions, _ ->
                        PermissionDialogFragment.showRationaleDialog(permissionOptions.rationaleDialogData)
                            .apply {
                                show(supportFragmentManager, PERMISSIONS_DIALOG_TAG)
                            }
                    }
                }
            }
        }
    }


    private inline fun <A, B, C, R> ifNotNull(a: A?, b: B?, c: C?, code: (A, B, C) -> R) {
        if (a != null && b != null && c != null) {
            code(a, b, c)
        }
    }

    override fun onPositiveButtonClick(dialogType: Int) {
        if (Constants.SETTINGS == dialogType) {
            settingsLauncher.launch(packageName)
        } else {
            permissionsLauncher.launch(permissions)
        }
    }

    override fun onNegativeButtonClick(dialogType: Int) {
        finish()
    }


}