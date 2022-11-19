package com.sliver.config

import kotlin.reflect.KClass

class ConfigBuilder {
    private var target: ConfigTarget? = null

    fun setConfigTarget(target: ConfigTarget) = apply { this.target = target }

    fun <T : Any> build(config: KClass<T>): T? {
        return null
    }
}