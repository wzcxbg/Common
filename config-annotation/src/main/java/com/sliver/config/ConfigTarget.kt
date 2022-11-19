package com.sliver.config

interface ConfigTarget {
    fun initialize(name: String?)
    fun set(key: String, value: Any?)
    fun get(key: String, default: Any?): Any?
}