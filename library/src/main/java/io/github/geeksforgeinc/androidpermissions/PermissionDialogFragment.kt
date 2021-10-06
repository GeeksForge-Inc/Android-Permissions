package io.github.geeksforgeinc.androidpermissions

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

class PermissionDialogFragment : DialogFragment() {

    companion object {
        private const val DIALOG_DATA_BUNDLE_KEY = "dialog_data"
        private const val DIALOG_TYPE_BUNDLE_KEY = "settings_enabled"
        private const val TAG = "PermissionDialog"

        @JvmStatic
        fun showSettingsDialog(DialogData: DialogData) =
            PermissionDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DIALOG_DATA_BUNDLE_KEY, DialogData)
                    putInt(DIALOG_TYPE_BUNDLE_KEY, Constants.SETTINGS)
                }
            }

        @JvmStatic
        fun showRationaleDialog(DialogData: DialogData) =
            PermissionDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DIALOG_DATA_BUNDLE_KEY, DialogData)
                    putInt(DIALOG_TYPE_BUNDLE_KEY, Constants.RATIONALE)
                }
            }
    }

    private var DialogData: DialogData? = null

    @DialogType
    private var dialogType: Int = Constants.RATIONALE
    private var dialogClickListener: DialogClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            if (it.containsKey(DIALOG_DATA_BUNDLE_KEY)) {
                DialogData = it.getParcelable(DIALOG_DATA_BUNDLE_KEY)
                dialogType = it.getInt(DIALOG_TYPE_BUNDLE_KEY)
            }
        }
        return AlertDialog.Builder(context)
            .setTitle(DialogData?.title)
            .setCancelable(false)
            .setMessage(DialogData?.message)
            .setPositiveButton(DialogData?.positiveButtonText) { dialog, which ->
                dialog.dismiss()
                dialogClickListener?.onPositiveButtonClick(dialogType)
            }
            .setNegativeButton(DialogData?.negativeButtonText) { dialog, which ->
                dialog.dismiss()
                dialogClickListener?.onNegativeButtonClick(dialogType)
            }.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dialogClickListener = requireActivity() as DialogClickListener
        } catch (exception: ClassCastException) {
            exception.message?.let { Log.e(TAG, it) }
        }
    }


    interface DialogClickListener {
        fun onPositiveButtonClick(dialogType: Int)
        fun onNegativeButtonClick(dialogType: Int)
    }

}