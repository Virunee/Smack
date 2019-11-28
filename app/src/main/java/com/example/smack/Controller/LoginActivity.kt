package com.example.smack.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.smack.R
import com.example.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginBtnClicked(view: View) {
        val email = loginEmailText.text.toString()
        val password = passwordEmailText.text.toString()
        AuthService.loginUser(this, email, password) { loginSuccess ->
            if(loginSuccess) {
                AuthService.finderUserByEmail(this) { findSuccess ->
                    if(findSuccess) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    fun createUserBtnClicked(view: View) {
        startActivity(Intent(this, CreateUserActivity::class.java))
        finish()
    }
}
