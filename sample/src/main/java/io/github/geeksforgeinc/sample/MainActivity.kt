package io.github.geeksforgeinc.sample

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.github.geeksforgeinc.androidpermissions.DialogPermissionOptions
import io.github.geeksforgeinc.androidpermissions.PermissionHandler
import io.github.geeksforgeinc.sample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

private lateinit var permissionHandler: PermissionHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val permissionOptions = DialogPermissionOptions(getString(R.string.permission_needed),
        getString(R.string.permission_denied_permanently_message),
        getString(R.string.not_now),
            getString(R.string.settings),
            true,
            getString(R.string.permission_needed),
            getString(R.string.permission_denied_once_message),
            getString(R.string.later),
            getString(R.string.allow_now)
        )
        permissionHandler = PermissionHandler(this, this.packageName, permissionOptions) {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_LONG).show()
        }
        lifecycle.addObserver(permissionHandler)
        binding.button.setOnClickListener {
            permissionHandler.requestPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        }


}