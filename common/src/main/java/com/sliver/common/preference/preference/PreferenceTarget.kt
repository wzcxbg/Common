package com.sliver.common.preference.preference

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import com.sliver.common.preference.Persistable
import com.sliver.common.preference.target.BaseTarget
import kotlin.reflect.KClass

class PreferenceTarget(context: Context, name: String) : BaseTarget(name) {
    private val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    override fun buildSupportTypes(): ArrayMap<KClass<*>, Persistable<*>> {
        return ArrayMap<KClass<*>, Persistable<*>>().apply {
            put(Int::class, object : Persistable<Int> {
                override fun set(key: String, value: Int?) {
                    editor.putInt(key, value!!).apply()
                }

                override fun get(key: String, default: Int?): Int {
                    return preferences.getInt(key, default ?: 0)
                }
            })

            put(String::class, object : Persistable<String> {
                override fun set(key: String, value: String?) {
                    editor.putString(key, value).apply()
                }

                override fun get(key: String, default: String?): String? {
                    return preferences.getString(key, default)
                }
            })
        }
    }
}