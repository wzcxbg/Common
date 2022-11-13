package com.sliver.common.startup

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.sliver.common.preference.PreferenceUtil

@Keep
class PreferenceUtilInitializer : Initializer<PreferenceUtil> {
    override fun create(context: Context): PreferenceUtil {
        PreferenceUtil.initialize(context)
        return PreferenceUtil
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}