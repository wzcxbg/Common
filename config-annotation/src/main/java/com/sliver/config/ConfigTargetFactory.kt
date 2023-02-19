package com.sliver.config

abstract class ConfigTargetFactory {
    abstract fun create(name: String): ConfigTarget
}