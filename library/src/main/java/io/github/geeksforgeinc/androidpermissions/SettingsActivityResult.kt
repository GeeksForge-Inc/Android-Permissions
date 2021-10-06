package io.github.geeksforgeinc.androidpermissions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class SettingsActivityResult : ActivityResultContract<String, Unit>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri
                .fromParts("package", context.packageName, null)
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {

    }
}