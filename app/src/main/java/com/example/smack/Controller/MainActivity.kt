package com.example.smack.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.ui.AppBarConfiguration
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smack.Adapters.MessageAdapter
import com.example.smack.Model.Channel
import com.example.smack.Model.Message
import com.example.smack.R
import com.example.smack.Services.AuthService
import com.example.smack.Services.MessageService
import com.example.smack.Services.UserDataService
import com.example.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter : ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter
    var selectedChannel : Channel? = null

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        hideKeyboard()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated",onNewMessage)

        setupAdapters()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter("BROADCAST_USER_DATA_CHANGE"))

        if (App.sharedPreferences.isLoggedIn) {
            AuthService.finderUserByEmail(this){}
        }

        channel_list.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()

        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
        super.onDestroy()
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            // This is what we want to happen when we receive that broadcast.
            // In this case - update the UI
            if(App.sharedPreferences.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                loginButtonNavHeader.text = "Logout"
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))

                MessageService.getChannels {complete ->
                    if(complete) {
                        if(MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            // we've grabbed the channel list so reload your data
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }
                }


            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
        // TODO: download messages for channel
        if (selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id) {complete ->
                if(complete) {
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavClicked(view: View) {
        if(App.sharedPreferences.isLoggedIn) {
            //logging out
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginButtonNavHeader.text = "Login"
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        } else {
            //logging in
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    fun addChannelClicked(view: View) {
        if(App.sharedPreferences.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") {_, _ ->
                    // perform some logic when clicked
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescText)
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    // Create channel with the channel name and description
                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel") { dialogInterface, id ->
                    // cancel and close the dialog
                }.show()


        } else {
            //TOAST
        }
    }

    fun sendMessageBtnClicked(view: View) {
        if(App.sharedPreferences.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage", messageTextField.text.toString(), userId, channelId,
                UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
        }
        messageTextField.text.clear()
        hideKeyboard()
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if(App.sharedPreferences.isLoggedIn) {
            runOnUiThread{
                var channelName = args[0] as String
                var channelDescription = args[1] as String
                var channelId = args[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)

                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }

    }

    private val onNewMessage = Emitter.Listener { args ->
        if(App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                var channelId = args[2] as String
                if(channelId == selectedChannel?.id) {
                    var messageBody = args[0] as String

                    var userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)

                    MessageService.messages.add(newMessage)

                    messageAdapter.notifyDataSetChanged()
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }

            }
        }
    }
}
