package io.github.geeksforgeinc.androidpermissions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogData(
    val title: String,
    val message: String,
    val negativeButtonText: String,
    val positiveButtonText: String
) : Parcelable