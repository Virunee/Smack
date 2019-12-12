package com.example.smack.Controller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Services.UserDataService
import com.example.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColour = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view: View) {
        var random = Random()
        var colour = random.nextInt(2)
        val avatar = random.nextInt(28)

        if(colour == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImageView.setImageResource(resourceId)
    }

    fun createUserBtnClicked(view: View) {
        enableSpinner(true)
        val userName = createUsernameText.text.toString()
        val email = createEmailText.getText().toString()
        val password = createPasswordText.text.toString()

        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            if(isEmailValid(email)) {
                AuthService.registerUser(email, password) { registerSuccess ->
                    if (registerSuccess) {
                        AuthService.loginUser(email, password) {loginSuccess ->
                            if (loginSuccess) {
                                AuthService.createUser(email, userName, userAvatar, avatarColour) { createUserSuccess ->
                                    if(createUserSuccess) {

                                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                        LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                        enableSpinner(false)
                                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
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
                        errorToast()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                enableSpinner(false)
            }

        } else {
            Toast.makeText(this, "Make sure Username, Email and Password fields are filled out", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }



    }

    fun generateBackgroundColour(view: View) {
        val random = Random()
        var r = random.nextInt(255)
        var g = random.nextInt(255)
        var b = random.nextInt(255)

        createAvatarImageView.setBackgroundColor((Color.rgb(r, g, b)))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColour = "[$savedR, $savedG, $savedB, 1]"
    }

    fun enableSpinner(enable: Boolean) {
        if(enable) {
            createSpinner.visibility = View.VISIBLE
        } else {
            createSpinner.visibility = View.INVISIBLE

        }
        createUserBtn.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        createChangeBackgroundColourBtn.isEnabled = !enable
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
