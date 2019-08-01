package com.coinlogiq.updateatmsproyect.ui.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.ui.MainActivity
import com.coinlogiq.updateatmsproyect.ui.extensions.*
import com.coinlogiq.updateatmsproyect.ui.activities.sing.SingUp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login_activity.*




const val RC_GOOGLE_SING_IN = 99
class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    //private val mGoogleApiClient : GoogleApiClient by lazy {getGoogleApliClient()}

    private lateinit var mGoogleApiClient : GoogleSignInClient

    private val mAuth : FirebaseAuth by lazy{ FirebaseAuth.getInstance()}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        //789596445938-45vqo9ppjtmaf39uiih5mromvld0rjn4.apps.googleusercontent.com
        //730654376106-a4ubj21hsabdpme2j8s61lvoonv2hue6.apps.googleusercontent.com
        Log.d("Token",gso.serverClientId)

        mGoogleApiClient = GoogleSignIn.getClient(this, gso)


        btn_login.setOnClickListener {
            val email = editTextEmail.text.toString()
            val pass = editTextPassword.text.toString()

            if(isValidateEmail(email) && isValidatePassword(pass)){
                logInByEmail(email,pass)
            }else{
                toast("Please make sure all date is correct")
            }
        }

        textViewForgotPass.setOnClickListener {
            gotoActivity<ForgotPassActivity>()
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        }

        btnSingUp.setOnClickListener {
            gotoActivity<SingUp>()
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        }

        editTextEmail.validate {
            editTextEmail.error = if(isValidateEmail(it)) null else "The email is not valid"
        }

        editTextPassword.validate {
            editTextPassword.error = if(isValidatePassword(it)) null else "The password should contain 1 lowercase, 1 uppercase, 1 number, 1 special character and 4 characters at least"
        }

        btnLoginGoogle.setOnClickListener {

            signIn()

            // val signInIntent = mGoogleApiClient.signInIntent
            //startActivityForResult(signInIntent,RC_GOOGLE_SING_IN)

        }

    }
    private fun signIn() {
        val signInIntent = mGoogleApiClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SING_IN)
    }

    private fun logInByEmail(email:String, pass:String){
        mAuth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    if(mAuth.currentUser!!.isEmailVerified){
                        gotoActivity<MainActivity>{
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    }else{
                        toast("User must confirm email first")
                    }
                }else{
                    toast("An unexpected error ocurred, please try again")
                }
            }
    }

    private fun logInByGoogleAccuntIntoFirebase(googleAccount: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this){
            toast("Signed By Google !!")
        }
    }

    /*private fun getGoogleApliClient() : GoogleApiClient {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleApiClient.Builder(this)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_GOOGLE_SING_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                logInByGoogleAccuntIntoFirebase(account!!)
            }catch (e: ApiException){
                Log.w("Google sign in failed", e)
            }

        }
        /*if(requestCode == RC_GOOGLE_SING_IN){
            Log.d("RequestCode",requestCode.toString())
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                Log.d("SingGoogle",result.toString())
                val account = result.signInAccount
                logInByGoogleAccuntIntoFirebase(account!!)
            }else{
                toast("ERROR LOG")
            }
        }else{
            toast("ERROR REQUEST CODE - $requestCode")
        }*/
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        toast("Connection Failed")
    }
}