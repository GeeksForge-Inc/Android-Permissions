package io.github.geeksforgeinc.androidpermissions;

import androidx.annotation.IntDef

@IntDef(value = [Constants.RATIONALE, Constants.SETTINGS])
@Retention(AnnotationRetention.SOURCE)
annotation class PermissionRequestType