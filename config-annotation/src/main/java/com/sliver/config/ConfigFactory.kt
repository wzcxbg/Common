package com.sliver.config

abstract class ConfigFactory {
    abstract fun <T> create(clazz: Class<T>): T
}