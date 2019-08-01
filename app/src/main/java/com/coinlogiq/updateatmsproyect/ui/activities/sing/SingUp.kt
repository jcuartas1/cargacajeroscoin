package com.coinlogiq.updateatmsproyect.ui.activities.sing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.ui.extensions.*
import com.coinlogiq.updateatmsproyect.ui.activities.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sing_up.*

class SingUp : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        btnGolongIn.setOnClickListener {
            gotoActivity<LoginActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
           overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btn_sUp.setOnClickListener {
            val email = editTextEmail.text.toString()
            Log.d("email", email)
            val pass = editTextPassword.text.toString()
            Log.d("pass", pass)
            val confirmPass = editTextConfirmPass.text.toString()
            Log.d("confirm", confirmPass)

            if(isValidateEmail(email) && isValidatePassword(pass) && isValidateConfirmPassword(pass, confirmPass)){
                singUpByEmail(email,pass)
            }else{
                toast("Please make sure all date is correct")
            }
        }

        editTextEmail.validate {
            editTextEmail.error = if(isValidateEmail(it)) null else "The email is not valid"
        }

        editTextPassword.validate {
            editTextPassword.error = if(isValidatePassword(it)) null else "The password should contain 1 lowercase, 1 uppercase, 1 number, 1 special character and 6 characters at least"
        }

        editTextConfirmPass.validate {
            editTextConfirmPass.error = if(isValidateConfirmPassword(editTextPassword.text.toString(),it)) null else "Confirm Password does not match with password"
        }

    }

    private fun singUpByEmail(email:String, password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                /*if(!task.isSuccessful){
                    Log.d("Firebase", task.exception?.message)
                }*/

                if (task.isSuccessful) {
                    mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this){
                        toast("An email has been sent to you. Please, confirm before sing in.")
                        gotoActivity<LoginActivity>{
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    toast("An unexpected error occurred, Please, try again")
                }
            }
    }

}
