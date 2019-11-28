package com.example.smack.Controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.smack.R
import com.example.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColour = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
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
        AuthService.registerUser(this, createEmailText.getText().toString(), createPasswordText.getText().toString()) { complete ->
            if (complete) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: Account not created successfully", Toast.LENGTH_SHORT).show()
            }
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
}
