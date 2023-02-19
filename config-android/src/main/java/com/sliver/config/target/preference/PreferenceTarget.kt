package com.sliver.config.target.preference

import android.content.Context
import android.content.SharedPreferences
import com.sliver.config.ConfigTarget

class PreferenceTarget(context: Context, name: String?) : ConfigTarget {
    private val preferenceName = name ?: (context.packageName + "_preferences")
    private var preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()

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