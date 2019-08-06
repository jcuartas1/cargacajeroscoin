package com.coinlogiq.updateatmsproyect.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.coinlogiq.updateatmsproyect.enums.PermissionStatusEnum
import com.coinlogiq.updateatmsproyect.ui.extensions.gotoActivity
import com.coinlogiq.updateatmsproyect.ui.activities.login.LoginActivity
import com.coinlogiq.updateatmsproyect.ui.extensions.toast
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainEmptyActivity : Activity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if(mAuth.currentUser == null){
            gotoActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            if(mAuth.currentUser!!.isEmailVerified){
                gotoActivity<MainActivity>{
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }else {
                gotoActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        finish()
    }

}
