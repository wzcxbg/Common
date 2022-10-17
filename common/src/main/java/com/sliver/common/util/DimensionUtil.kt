package com.sliver.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import kotlin.math.roundToInt

/**
 * 尺寸工具（参考：https://github.com/bonepeople/DimensionUtil）
 */
object DimensionUtil {
    private val displayMetrics = Resources.getSystem().displayMetrics

    fun dp2px(dpValue: Float): Int {
        val result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics)
        return result.roundToInt()
    }

    fun sp2px(context: Context, spValue: Float): Int {
        val result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, displayMetrics)
        return result.roundToInt()
    }

    fun px2dp(pxValue: Int): Float {
        return pxValue / displayMetrics.density
    }

    fun px2sp(pxValue: Int): Float {
        return pxValue / displayMetrics.scaledDensity
    }

    @SuppressLint("InternalInsetResource")
    fun getStatusBarHeight(): Int {
        return runCatching {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            resources.getDimensionPixelSize(resourceId)
        }.getOrDefault(0)
    }

    @SuppressLint("InternalInsetResource")
    fun getNavigationBarHeight(): Int {
        return runCatching {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            Resources.getSystem().getDimensionPixelSize(resourceId)
        }.getOrDefault(0)
    }

    fun getDisplayWidth(): Int {
        return displayMetrics.widthPixels
    }

    fun getDisplayHeight(): Int {
        return displayMetrics.heightPixels
    }

    fun getDisplayWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val realMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(realMetrics)
        return realMetrics.widthPixels
    }

    fun getDisplayHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val realMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(realMetrics)
        return realMetrics.heightPixels
    }
}