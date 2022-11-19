package com.sliver.config

object ConfigProvider {
    private val configMap = HashMap<Class<*>, Any>()

    fun <T> getConfig(clazz: Class<T>): T? {
        return null
    }
}