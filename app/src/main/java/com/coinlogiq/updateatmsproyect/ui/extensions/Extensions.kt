package com.coinlogiq.updateatmsproyect.ui.extensions

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import java.sql.RowId
import java.util.regex.Pattern

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message,duration).show()

fun Activity.toast(resourceId: Int, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this,resourceId,duration).show()

fun ViewGroup.inflate(layoutId: Int) = LayoutInflater.from(context).inflate(layoutId,this,false)!!

inline fun <reified T : Activity> Activity.gotoActivity(noinline  init: Intent.() -> Unit = {}){
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

//
fun EditText.validate (validation: (String) -> Unit){
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            validation(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    })
}

 fun Activity.isValidateEmail(email:String) : Boolean{
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

 fun Activity.isValidatePassword(password:String) : Boolean{

    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*.])(?=\\S+$).{6,}$"
    val pattern = Pattern.compile(passwordPattern)
    return pattern.matcher(password).matches()
}

 fun Activity.isValidateConfirmPassword(password: String, confirmPassword: String) : Boolean {
    return password == confirmPassword
}