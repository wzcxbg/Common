package com.sliver.common.preference

import com.sliver.common.preference.target.Target
import com.sliver.common.preference.target.TargetFactory
import com.sliver.config_annotation.PreferenceKey
import com.sliver.config_annotation.PreferenceName
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.stream.Stream

class PreferenceBuilder() {
    var targetFactory: TargetFactory? = null

    fun setTargetFactory(factory: TargetFactory) = apply { this.targetFactory = factory }

    inline fun <reified T> build(): T {
        return Proxy.newProxyInstance(T::class.java.classLoader,
            arrayOf(Preference::class.java), object : InvocationHandler {
                private val target = createTarget()

                private fun createTarget(): Target? {
                    val preferenceClass = T::class.java
                    val preferenceNameValue =
                        Stream.of(preferenceClass)
                            .map { it.getAnnotation(PreferenceName::class.java) }
                            .filter { it != null }
                            .map { it.name }
                            .findFirst()
                            .orElse(preferenceClass.simpleName.lowercase())
                    return targetFactory?.create(preferenceNameValue)
                }

                override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
                    val preferenceClass = T::class.java
                    val preferenceKeyValue =
                        Stream.of(preferenceClass.declaredClasses)
                            .flatMap { it.toList().stream() }
                            .filter { it.name == "${preferenceClass.name}${'$'}DefaultImpls" }
                            .filter { it.simpleName == "DefaultImpls" }
                            .map {
                                it.getDeclaredMethod(Stream.of(method)
                                    .peek { it.isAccessible = true }
                                    .map { "${method.name}${'$'}annotations" }
                                    .map { innerClass ->
                                        if (!innerClass.startsWith("set")) innerClass
                                        else innerClass.replaceFirst("set", "get")
                                    }
                                    .findFirst()
                                    .orElseThrow())
                            }
                            .peek { it.isAccessible = true }
                            .map { it.getAnnotation(PreferenceKey::class.java) }
                            .map { it?.key }
                            .findFirst()
                            .orElse(Stream.of(method.name)
                                .filter { it.startsWith("set") }
                                .filter { it.startsWith("get") }
                                .map { it.substring(3) }
                                .map { it.lowercase() }
                                .findFirst().orElse(method.name))!!

                    if (method.name.startsWith("set")) {
                        return target?.set(preferenceKeyValue, args?.get(0)!!)
                    } else if (method.name.startsWith("get")) {
                        return target?.get(preferenceKeyValue, method.returnType.kotlin)
                    }
                    return null
                }
            }) as T
    }
}