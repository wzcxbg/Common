package com.sliver.common.preference

import com.sliver.config_annotation.PreferenceKey
import com.sliver.config_annotation.PreferenceName

@PreferenceName("user")
open class Config {
    @PreferenceKey("username")
    var username: String = "zhangsan"

    @PreferenceKey("password")
    var password: String = "12345678"

    @PreferenceKey("age")
    var age: Int = 24
}