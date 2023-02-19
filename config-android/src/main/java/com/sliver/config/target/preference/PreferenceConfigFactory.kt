package com.sliver.config.target.preference

import android.content.Context
import com.sliver.config.ConfigBasic
import com.sliver.config.ConfigFactory
import com.sliver.config.ConfigTargetFactory
import com.sliver.config.annotation.PreferenceName

object PreferenceConfigFactory : ConfigFactory() {
    private var targetFactory: ConfigTargetFactory? = null

    fun initialize(context: Context) {
        targetFactory = PreferenceTargetFactory(context)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> create(clazz: Class<T>): T {
        val configClass = Class.forName("${clazz.canonicalName}Config")
        val preferenceName = clazz.getAnnotation(PreferenceName::class.java)
        val immPreferenceName = requireNotNull(preferenceName)  //需要自己保证传入的类型包含PreferenceName注解
        val immTargetFactory = requireNotNull(targetFactory)    //要求自定控制调用顺序，先初始化Factory再执行create
        val target = immTargetFactory.create(immPreferenceName.name)
        val constructor = configClass.getConstructor()
        constructor.isAccessible = true
        val instance = constructor.newInstance()
        instance as ConfigBasic
        instance.setTarget(target)
        return instance as T
    }
}