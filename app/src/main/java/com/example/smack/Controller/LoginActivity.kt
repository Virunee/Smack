package com.example.smack.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.smack.R
import com.example.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    //TODO: Email field should be auto focussed when this activity starts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginLoginBtnClicked(view: View) {

        enableSpinner(true)
        val email = loginEmailText.text.toString()
        val password = passwordEmailText.text.toString()
        hideKeyboard()

        if(email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(this, email, password) { loginSuccess ->
                if(loginSuccess) {
                    AuthService.finderUserByEmail(this) { findSuccess ->
                        if(findSuccess) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Please fill in both email and password", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }

    }

    fun createUserBtnClicked(view: View) {
        startActivity(Intent(this, CreateUserActivity::class.java))
        finish()
    }

    fun enableSpinner(enable: Boolean) {
        if(enable) {
            loginSpinner.visibility = View.VISIBLE
        } else {
            loginSpinner.visibility = View.INVISIBLE

        }
        loginLoginBtn.isEnabled = !enable
        createUserBtn.isEnabled = !enable
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}
