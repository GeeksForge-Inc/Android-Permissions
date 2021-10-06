package io.github.geeksforgeinc.androidpermissions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DialogPermissionOptions(
    val rationaleDialogData: DialogData,
    val settingDialogData: DialogData? = null
) : PermissionOptions, Parcelable {

    override fun settingsEnabled(): Boolean {
        return settingDialogData == null
    }
}
