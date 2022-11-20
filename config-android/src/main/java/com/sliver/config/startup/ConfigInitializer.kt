package com.sliver.config.startup

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer

@Keep
class ConfigInitializer : Initializer<Unit> {
    override fun create(context: Context) {

    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}