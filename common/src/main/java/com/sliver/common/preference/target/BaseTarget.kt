package com.sliver.common.preference.target

import android.util.ArrayMap
import android.util.Log
import com.sliver.common.preference.Persistable
import kotlin.reflect.KClass

abstract class BaseTarget(private val name: String) : Target {
    private val supportTypes by lazy { buildSupportTypes() }

    abstract fun buildSupportTypes(): ArrayMap<KClass<*>, Persistable<*>>

    override fun set(key: String, value: Any) {
        val persistable = supportTypes[value::class] ?: return
        @Suppress("UNCHECKED_CAST")
        persistable as Persistable<Any>
        persistable.set(key, value)
    }

    override fun get(key: String, default: Any?): Any? {
        //val kClass = if (default != null) default::class else Any::class
        default ?: return null
        val persistable = supportTypes[default] ?: return null
        @Suppress("UNCHECKED_CAST")
        persistable as Persistable<Any>
        return persistable.get(key, null)
    }
}