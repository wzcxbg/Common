package com.sliver.config.target.preference

import android.content.Context
import androidx.startup.Initializer

class PreferenceConfigFactoryInitializer : Initializer<Unit> {
    override fun create(context: Context): Unit {
        return PreferenceConfigFactory.initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
