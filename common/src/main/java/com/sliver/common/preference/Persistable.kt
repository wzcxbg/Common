package com.sliver.common.preference

interface Persistable<T> {
    fun set(key: String, value: T?)
    fun get(key: String, default: T?): T?
}