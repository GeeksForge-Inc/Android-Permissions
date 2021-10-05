package io.github.geeksforgeinc.androidpermissions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class ShadowActivity : AppCompatActivity() {
    companion object{
        private const val SETTINGS_BUNDLE_KEY = "settings_bundle_key"
        private const val RATIONALE_BUNDLE_KEY = "rationale_bundle_key"
        private const val PERMISSION_OPTIONS_BUNDLE_KEY = "permission_options_bundle_key"
        private const val PERMISSIONS_BUNDLE_KEY = "permissions_bundle_key"
        @JvmStatic
        fun launchSettings(context: Context, permissionOptions: PermissionOptions, packageName : String, permissions : Array<String>) =
            Intent(context, ShadowActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(SETTINGS_BUNDLE_KEY, packageName)
                putExtra(PERMISSION_OPTIONS_BUNDLE_KEY, permissionOptions as DialogPermissionOptions)
                putExtra(PERMISSIONS_BUNDLE_KEY, permissions)
                context.startActivity(this)
            }

        @JvmStatic
        fun showRationale(context: Context, permissionOptions: PermissionOptions, packageName : String, permissions : Array<String>) =
            Intent(context, ShadowActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(RATIONALE_BUNDLE_KEY, packageName)
                putExtra(PERMISSIONS_BUNDLE_KEY, permissions)
                putExtra(PERMISSION_OPTIONS_BUNDLE_KEY, permissionOptions as DialogPermissionOptions)
                context.startActivity(this)
            }
    }
    private var permissions : Array<String>? = null
    private var permissionsOptions : DialogPermissionOptions? = null
    private var applicationPackageName: String? = null

    private val settingsLauncher = registerForActivityResult(SettingsActivityResult()) {
        permissionsLauncher.launch(permissions)
    }
    private val permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
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
                    permissionsOptions,
                    permissions
                ) { packageName, options, perms ->
                    showRationale(
                        this, options, packageName, perms
                    )
                }
            } else {
                if (permissionsOptions?.settingsEnabled() == true) {
                    ifNotNull(
                        applicationPackageName,
                        permissionsOptions,
                        permissions
                    ) { packageName, options, perms ->
                        launchSettings(
                            this, options, packageName, perms
                        )
                    }
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            finish()
        }?: run {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }


    private fun handleIntent(intent: Intent) {
        if (intent.hasExtra(SETTINGS_BUNDLE_KEY) && intent.hasExtra(
                PERMISSION_OPTIONS_BUNDLE_KEY) && intent.hasExtra(PERMISSIONS_BUNDLE_KEY)) {
                    applicationPackageName = intent.getStringExtra(SETTINGS_BUNDLE_KEY)
                    permissionsOptions = intent.getParcelableExtra(PERMISSION_OPTIONS_BUNDLE_KEY)
                    permissions = intent.getStringArrayExtra(PERMISSIONS_BUNDLE_KEY)
            ifNotNull(applicationPackageName, permissionsOptions, permissions) { packageName, permissionOptions, permissions ->
                PermissionUtils.showDialog(this, permissionOptions.blockedTitle,
                    permissionOptions.blockedMessage,
                    permissionOptions.blockedPositiveButtonText,
                    permissionOptions.blockedNegativeButtonText,
                    {
                        settingsLauncher.launch(packageName)
                    },
                    {
                        finish()
                    })
            }
        } else if (intent.hasExtra(RATIONALE_BUNDLE_KEY) && intent.hasExtra(
                PERMISSION_OPTIONS_BUNDLE_KEY) && intent.hasExtra(PERMISSIONS_BUNDLE_KEY)) {
            applicationPackageName = intent.getStringExtra(RATIONALE_BUNDLE_KEY)
            permissionsOptions = intent.getParcelableExtra(PERMISSION_OPTIONS_BUNDLE_KEY)
            permissions = intent.getStringArrayExtra(PERMISSIONS_BUNDLE_KEY)
            ifNotNull(applicationPackageName, permissionsOptions, permissions) { packageName, permissionOptions, permissions ->
                PermissionUtils.showDialog(this, permissionOptions.deniedTitle,
                    permissionOptions.deniedMessage,
                    permissionOptions.deniedPositiveButtonText,
                    permissionOptions.deniedNegativeButtonText,
                    {
                      permissionsLauncher.launch(permissions)
                    },
                    {
                        finish()
                    })
            }
        }
    }


    private inline fun <A, B, C, R> ifNotNull(a: A?, b: B?, c: C?, code: (A, B, C) -> R) {
        if (a != null && b != null && c != null) {
                code(a,b,c)
        }
    }



}