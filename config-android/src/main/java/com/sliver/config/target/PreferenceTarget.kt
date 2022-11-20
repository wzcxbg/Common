package com.sliver.config.target

import android.content.Context
import android.content.SharedPreferences
import com.sliver.config.ConfigTarget

class PreferenceTarget(private val context: Context) : ConfigTarget {
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun initialize(name: String?) {
        val preferenceName = name ?: (context.packageName + "_preferences")
        preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    override fun set(key: String, value: Any?) {
        if (value is String) {
            editor.putString(key, value).apply()
        } else if (value is Int) {
            editor.putInt(key, value).apply()
        } else if (value is Long) {
            editor.putLong(key, value).apply()
        } else if (value is Float) {
            editor.putFloat(key, value).apply()
        } else if (value is Boolean) {
            editor.putBoolean(key, value).apply()
        } else {
            editor.remove(key)
        }
    }

    override fun get(key: String, default: Any?): Any? {
        return if (default is String) {
            preferences.getString(key, default)
        } else if (default is Int) {
            preferences.getInt(key, default)
        } else if (default is Long) {
            preferences.getLong(key, default)
        } else if (default is Float) {
            preferences.getFloat(key, default)
        } else if (default is Boolean) {
            preferences.getBoolean(key, default)
        } else {
            default
        }
    }
}