package io.github.geeksforgeinc.androidpermissions

import android.content.Context

class PermissionHandler private constructor(private val context: Context) {
    lateinit var permissionResultCallback : () -> Unit
    companion object : SingletonHolder<PermissionHandler, Context>(::PermissionHandler)
    fun requestPermissions(permissions : Array<String>,  callback : () -> Unit) {
        this.permissionResultCallback = callback
        ShadowActivity.startActivity(context, permissions)
    }

    @JvmName("requestPermissions1")
    fun requestPermissions(vararg  permissions : String, callback : () -> Unit) {
        this.permissionResultCallback = callback
        ShadowActivity.startActivity(context, arrayOf(*permissions))
    }

}