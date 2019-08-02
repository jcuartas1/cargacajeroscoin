package com.coinlogiq.updateatmsproyect.ui.dialogues


import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.model.NewRateEvent
import com.coinlogiq.updateatmsproyect.model.Rate
import com.coinlogiq.updateatmsproyect.ui.extensions.toast
import com.coinlogiq.updateatmsproyect.utils.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_rate.view.*
import java.util.*

class RateDialog : DialogFragment(){

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        setUpCurrentUser()

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_rate, null)

        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.dialog_title))
            .setView(view)
            .setPositiveButton(R.string.dialog_ok){ _, _ ->
                val textRate = view.editTextRateFeedBack.text.toString()
                if (textRate.isNotEmpty()){
                    val imgUrl = currentUser.photoUrl?.toString() ?: run { "" }
                    val rate = Rate(currentUser.uid,textRate, view.ratingBarFeedBack.rating, Date(),imgUrl)
                    RxBus.publish(NewRateEvent(rate))
                }
            }
            .setNegativeButton(R.string.dialog_cancel){ _, _ ->
                activity!!.toast("Pressed Cancel")
            }
            .create()

    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }
}