package androidx.startup

import android.content.Context

interface Initializer<T> {
    fun create(context: Context?): T
    fun dependencies(): List<Class<out Initializer<*>?>?>?
}