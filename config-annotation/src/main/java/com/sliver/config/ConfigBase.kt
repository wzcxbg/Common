package com.sliver.config

/**
 * Config类必须能获取到Target，并使用Target进行写入和读取
 * 又由于生成的Config必须继承用户的自定义的Config类，
 * 因此用委托的方式给Config添加获取Target的实现
 */
class ConfigBase : ConfigBasic {
    private var target: ConfigTarget? = null

    override fun setTarget(target: ConfigTarget) {
        this.target = target
    }

    override fun getTarget(): ConfigTarget {
        return requireNotNull(target)
    }
}