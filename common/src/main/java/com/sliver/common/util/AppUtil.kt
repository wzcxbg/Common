package com.sliver.common.util

import android.annotation.SuppressLint
import android.app.Application

object AppUtil {
    @SuppressLint("PrivateApi")
    fun getApp(): Application {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val method = activityThreadClass.getMethod("currentApplication")
        return method.invoke(null) as Application
    }
}