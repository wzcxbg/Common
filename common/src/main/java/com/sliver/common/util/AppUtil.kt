package com.sliver.common.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

object AppUtil {
    private var app: Application? = null

    fun initialize(context: Context) {
        app = context.applicationContext as Application
    }

    fun getApp(): Application {
        return app ?: getAppByReflection()
    }

    @SuppressLint("PrivateApi")
    private fun getAppByReflection(): Application {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val method = activityThreadClass.getMethod("currentApplication")
        return method.invoke(null) as Application
    }
}