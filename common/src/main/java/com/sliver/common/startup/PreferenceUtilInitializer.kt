package com.sliver.common.startup

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.sliver.common.preference.config.User

@Keep
class PreferenceUtilInitializer : Initializer<Int> {
    override fun create(context: Context): Int {
        return 0
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}