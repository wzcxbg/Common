package com.sliver.config

class ConfigBase : ConfigBasic {
    private var target: ConfigTarget? = null

    override fun setTarget(target: ConfigTarget) {
        this.target = target
    }

    override fun getTarget(): ConfigTarget {
        return requireNotNull(target)
    }
}