package io.github.geeksforgeinc.androidpermissions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DialogPermissionOptions(val blockedTitle : String,
                                   val blockedMessage : String,
                                   val blockedNegativeButtonText : String,
                                   val blockedPositiveButtonText : String,
                                   val settingOptionsEnabled : Boolean,
                                   val deniedTitle : String = "",
                                   val deniedMessage : String = "",
                                   val deniedNegativeButtonText : String = "",
                                   val deniedPositiveButtonText : String = ""
) : PermissionOptions, Parcelable {
    override fun settingsEnabled(): Boolean {
        return settingOptionsEnabled
    }
}
