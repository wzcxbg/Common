package com.sliver.config

import com.sliver.config.annotation.PreferenceName

object ConfigOf {
    inline operator fun <reified T : Any> invoke(): T {
        return ConfigMap.map[T::class] as T
    }
}

object Config {
    inline fun <reified T : Any> create(target: ConfigTarget): T {
        if (ConfigMap.map.containsKey(T::class)) return ConfigMap.map[T::class] as T
        val configClass = Class.forName("${T::class.java.canonicalName}Config")
        val preferenceName = configClass.getAnnotation(PreferenceName::class.java)
        target.initialize(preferenceName.name)
        val constructor = configClass.getConstructor()
        constructor.isAccessible = true
        val instance = constructor.newInstance()
        instance as ConfigBasic
        instance.setTarget(target)
        ConfigMap.map[T::class] = instance
        @Suppress("UNCHECKED_CAST")
        return instance as T
    }
}