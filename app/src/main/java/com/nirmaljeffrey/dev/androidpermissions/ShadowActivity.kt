package com.nirmaljeffrey.dev.androidpermissions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nirmaljeffrey.dev.androidpermissions.PermissionUtils.handlePermissionGrantResults
import android.content.Context
import android.content.Intent
import com.nirmaljeffrey.dev.androidpermissions.PermissionUtils.showDialog

import com.nirmaljeffrey.dev.androidpermissions.PermissionUtils.arePermissionsGranted
import com.nirmaljeffrey.dev.androidpermissions.PermissionUtils.launchIntentToPermissionSettings


class ShadowActivity : AppCompatActivity(), PermissionUtils.PermissionConsentListener {
    companion object{
        private const val PERMISSION_REQUEST_CODE = 12345678;
        private const val PERMISSIONS_BUNDLE_KEY = "permissions_bundle_key"

        @JvmStatic
        fun startActivity(context : Context, permissions: Array<String>) =
            Intent(context, ShadowActivity::class.java).apply {
                putExtra(PERMISSIONS_BUNDLE_KEY,permissions)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
    }
    private var permissions : Array<String>? = null
    private var isReturnedFromSettingsApp = false
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
         permissions = intent.getStringArrayExtra(PERMISSIONS_BUNDLE_KEY)
       permissions?.let {
           requestPermissions(it)
       }
    }

    private fun requestPermissions(permissions: Array<String>) {
        PermissionUtils.requestRuntimePermissions(
            this, this,
            PERMISSION_REQUEST_CODE, permissions
        )
    }


    override fun onResume() {
        super.onResume()
        if (isReturnedFromSettingsApp) {
            isReturnedFromSettingsApp = false;
            permissions?.let {
                requestPermissions(it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                if (requestCode == PERMISSION_REQUEST_CODE) {
            handlePermissionGrantResults(this, this, permissions, grantResults)
        }
    }



    override fun finish() {
        super.finish()
        // Reset the animation to avoid flickering.
        overridePendingTransition(0, 0)
    }


    override fun onPermissionPreviouslyDenied() {
        showDialog(this, getString(R.string.permission_needed),
            getString(R.string.permission_denied_once_message),
            getString(R.string.allow_now),
            getString(R.string.later),
            {
                permissions?.let {
                    arePermissionsGranted(this,
                        PERMISSION_REQUEST_CODE, it
                    )
                }
            },
            {
                finish()
            }
        )
    }

    override fun onPermissionDisabledPermanently() {
        showDialog(this, getString(R.string.permission_needed),
            getString(R.string.permission_denied_permanently_message),
            getString(R.string.settings),
            getString(R.string.not_now),
            {
                isReturnedFromSettingsApp = launchIntentToPermissionSettings(this)
            },
            {
                finish()
            })
    }

    override fun onPermissionGranted() {
        finish()
        PermissionHandler.getInstance(this).permissionResultCallback()
    }
}