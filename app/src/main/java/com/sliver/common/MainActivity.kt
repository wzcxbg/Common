package com.sliver.common

import android.util.Log
import com.sliver.common.base.BaseActivity
import com.sliver.common.databinding.ActivityMainBinding
import com.sliver.common.preference.Preference
import com.sliver.common.preference.PreferenceBuilder
import com.sliver.common.preference.preference.PreferenceTargetFactory

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initView() {
        val preference = PreferenceBuilder()
            .setTargetFactory(PreferenceTargetFactory(this))
            .build<Preference>()
        preference.username = "hanpi"
        preference.password = "123456"
        preference.age = 24

        Log.e(TAG, "initView: ${preference.username} ${preference.password} ${preference.age}")
    }
}