package com.sliver.common.preference.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferenceKey(val key: String)
