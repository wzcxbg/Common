package com.sliver.common.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PreferenceUtil {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun initialize(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()
    }

    fun get(key: String, default: Any?): Any? {
        val value = sharedPreferences.getString(key, null)
        return value ?: default
    }

    fun set(key: String, value: Any?) {
        editor
            .putString(key, value.toString())
            .apply()
    }
}