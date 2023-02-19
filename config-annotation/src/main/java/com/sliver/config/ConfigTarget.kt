package com.sliver.config

interface ConfigTarget {
    fun set(key: String, value: Any?)
    fun get(key: String, default: Any?): Any?
}