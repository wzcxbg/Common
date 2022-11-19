package com.sliver.common

import android.util.Log
import com.sliver.common.preference.config.User
import org.junit.Test

import org.junit.Assert.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val kClass = User::class
        for (declaredMemberProperty in kClass.declaredMemberProperties) {
            declaredMemberProperty.isAccessible = true
            println(declaredMemberProperty.call(User()))
        }
    }
}