package com.sliver.common.preference.target

interface TargetFactory {
    fun create(name: String): Target
}