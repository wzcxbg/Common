package com.sliver.config

import kotlin.reflect.KClass

object ConfigMap {
    val map = HashMap<KClass<*>, Any>()
}