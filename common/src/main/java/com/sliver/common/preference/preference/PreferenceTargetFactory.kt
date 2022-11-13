package com.sliver.common.preference.preference

import android.content.Context
import com.sliver.common.preference.target.Target
import com.sliver.common.preference.target.TargetFactory

class PreferenceTargetFactory(private val context: Context) : TargetFactory {
    override fun create(name: String): Target {
        return PreferenceTarget(context, name)
    }
}