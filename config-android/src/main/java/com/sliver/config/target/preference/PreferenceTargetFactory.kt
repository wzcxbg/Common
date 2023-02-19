package com.sliver.config.target.preference

import android.app.Application
import android.content.Context
import com.sliver.config.ConfigTarget
import com.sliver.config.ConfigTargetFactory

class PreferenceTargetFactory(context: Context) : ConfigTargetFactory() {
    private val app = context.applicationContext as Application

    override fun create(name: String): ConfigTarget {
        return PreferenceTarget(app, name)
    }
}