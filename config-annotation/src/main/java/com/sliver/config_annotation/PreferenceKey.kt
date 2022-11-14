package com.sliver.config_annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferenceKey(val key: String)
