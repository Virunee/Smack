package com.example.smack.Utilities

import android.content.Context

class SharedPrefs(context: Context) {
    val PREFS_FILENAME = "prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
}