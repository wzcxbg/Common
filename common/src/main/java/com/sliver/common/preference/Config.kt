package com.sliver.common.preference

import com.sliver.common.preference.annotation.PreferenceKey
import com.sliver.common.preference.annotation.PreferenceName

@PreferenceName("user")
open class Config {
    @PreferenceKey("username")
    var username: String = "zhangsan"

    @PreferenceKey("password")
    var password: String = "12345678"

    @PreferenceKey("age")
    var age: Int = 24
}