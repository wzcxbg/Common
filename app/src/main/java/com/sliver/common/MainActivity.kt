package com.sliver.common

import android.util.Log
import com.sliver.common.base.BaseActivity
import com.sliver.common.databinding.ActivityMainBinding
import com.sliver.common.preference.config.PreferenceTarget
import com.sliver.common.preference.config.UserConfig

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initView() {
        val preferenceTarget = PreferenceTarget(this)
        preferenceTarget.initialize("user")
        val userConfig = UserConfig(preferenceTarget)

        Log.e(TAG, "initView: ${userConfig.username} ${userConfig.password} ${userConfig.age}")

        userConfig.setUsername("lisi")
            .setPassword("87654321")
            .setAge(25)

        userConfig.username = "wangwu"
        userConfig.password = "11223344"
        userConfig.age = 25

        Log.e(TAG, "initView: ${userConfig.username} ${userConfig.password} ${userConfig.age}")
    }
}