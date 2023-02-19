package com.sliver.config.target.preference

object PreferenceConfigOf {
    val configMap = HashMap<Class<*>, Any>()

    inline operator fun <reified T : Any> invoke(): T {
        val config = configMap[T::class.java]
        if (config != null) return config as T
        val configNew = PreferenceConfigFactory.create(T::class.java)
        configMap[T::class.java] = configNew
        return configNew
    }
}
