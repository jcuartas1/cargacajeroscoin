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

        checkAllPermissions()

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

    private fun checkAllPermissions(){
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let{
                        report.areAllPermissionsGranted()
                        setPermissionStatus(PermissionStatusEnum.GRANTED)
                        /*for(permision in report.grantedPermissionResponses){
                            when(permision.permissionName){
                                Manifest.permission.CAMERA -> setPermissionStatus(PermissionStatusEnum.GRANTED)
                                Manifest.permission.INTERNET -> setPermissionStatus(PermissionStatusEnum.GRANTED)
                                Manifest.permission.ACCESS_COARSE_LOCATION -> setPermissionStatus(PermissionStatusEnum.GRANTED)
                                Manifest.permission.ACCESS_FINE_LOCATION -> setPermissionStatus(PermissionStatusEnum.GRANTED)
                            }
                        }*/
                        for(permission in report.deniedPermissionResponses){
                            when(permission.permissionName){
                                Manifest.permission.CAMERA ->{
                                    if(permission.isPermanentlyDenied){
                                        setPermissionStatus(PermissionStatusEnum.PERMENTLY_DENIED)
                                    }else{
                                        setPermissionStatus(PermissionStatusEnum.DENIED)
                                    }
                                }
                                Manifest.permission.INTERNET ->{
                                    if(permission.isPermanentlyDenied){
                                        setPermissionStatus(PermissionStatusEnum.PERMENTLY_DENIED)
                                    }else{
                                        setPermissionStatus(PermissionStatusEnum.DENIED)
                                    }
                                }
                                Manifest.permission.ACCESS_COARSE_LOCATION ->{
                                    if(permission.isPermanentlyDenied){
                                        setPermissionStatus(PermissionStatusEnum.PERMENTLY_DENIED)
                                    }else{
                                        setPermissionStatus(PermissionStatusEnum.DENIED)
                                    }
                                }
                                Manifest.permission.ACCESS_FINE_LOCATION ->{
                                    if(permission.isPermanentlyDenied){
                                        setPermissionStatus(PermissionStatusEnum.PERMENTLY_DENIED)
                                    }else{
                                        setPermissionStatus(PermissionStatusEnum.DENIED)
                                    }
                                }
                            }
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            })
    }

    private fun setPermissionStatus(status: PermissionStatusEnum){
        when(status){
            PermissionStatusEnum.GRANTED ->{
                toast("Permisos OK")
            }
            PermissionStatusEnum.DENIED ->{
                toast("Permisos Denegados")
            }
            PermissionStatusEnum.PERMENTLY_DENIED ->{
                toast("Permisos permanentemente denegados")
            }
        }
    }
}
