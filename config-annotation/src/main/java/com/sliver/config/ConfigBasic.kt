package com.sliver.config

interface ConfigBasic {
    fun setTarget(target: ConfigTarget)
    fun getTarget(): ConfigTarget
}