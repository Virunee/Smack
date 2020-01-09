package com.example.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smack.Controller.App
import com.example.smack.Model.Channel
import com.example.smack.Model.Message
import com.example.smack.Services.MessageService.messages
import com.example.smack.Utilities.URL_GET_CHANNEL_LIST
import com.example.smack.Utilities.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {
    // main purpose is to download the messages and channels

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {
        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNEL_LIST, null, Response.Listener { response ->

            try {
                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val id = channel.getString("_id")

                    val newChannel = Channel(name, desc, id)

                    this.channels.add(newChannel)
                }
                complete(true)
            } catch(e: JSONException) {
                Log.d("JSON", "Exception: " + e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not get channel list: ${error}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
            return "application/json; charset=utf-8"
        }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }

        }
        App.sharedPreferences.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {
        val url = "$URL_GET_MESSAGES$channelId"
        println("channelId: ${channelId}")
        println("auth Token: ${App.sharedPreferences.authToken}")
        println("requestUrl: ${url}")
        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            clearMessages()
          try {
              for (x in 0 until response.length()) {
                  val message = response.getJSONObject(x)
                  val messageBody = message.getString("messageBody")
                  val channelId = message.getString("channelId")
                  val userName = message.getString("userName")
                  val userAvatar = message.getString("userAvatar")
                  val userAvatarColor = message.getString("userAvatarColor")
                  val id = message.getString("_id")
                  val timeStamp = message.getString("timeStamp")

                  val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                  this.messages.add(newMessage)
              }
              complete(true)
          } catch(e: JSONException) {
              Log.d("JSON", "Exception: " + e.localizedMessage)
              complete(false)
          }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not get messages: ${error}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }
        App.sharedPreferences.requestQueue.add(messagesRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}

