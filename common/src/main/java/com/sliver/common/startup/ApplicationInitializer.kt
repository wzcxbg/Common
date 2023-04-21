package com.sliver.common.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.sliver.common.util.AppUtil

class ApplicationInitializer : Initializer<Application> {
    override fun create(context: Context): Application {
        AppUtil.initialize(context)
        return AppUtil.getApp()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}