package com.sliver.common.preference.target

interface Target {
    fun set(key: String, value: Any)
    fun get(key: String, default: Any?): Any?
}