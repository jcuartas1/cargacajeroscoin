package com.coinlogiq.updateatmsproyect.ui.dialogues


import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.ui.extensions.toast

class RateDialog : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.dialog_title))
            .setView(R.layout.dialog_rate)
            .setPositiveButton(R.string.dialog_ok){ dialog, which ->
                activity!!.toast("Pressed OK")
            }
            .setNegativeButton(R.string.dialog_cancel){ dialog, which ->
                activity!!.toast("Pressed Cancel")
            }
            .create()


    }
}