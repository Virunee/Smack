package com.example.smack.Controller

import android.app.Application
import com.example.smack.Utilities.SharedPrefs

class App : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        /* Initialise shared preferences - these will be globally available so we can save and
         retrieve them from anywhere
         */
        sharedPreferences = SharedPrefs(applicationContext)
        super.onCreate()
    }
}