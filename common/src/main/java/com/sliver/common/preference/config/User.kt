package com.sliver.common.preference.config

import com.sliver.config.annotation.PreferenceKey
import com.sliver.config.annotation.PreferenceName

@PreferenceName("user")
open class User {
    @PreferenceKey("username")
    open var username: String = "zhangsan"

    @PreferenceKey("password")
    open var password: String = "12345678"

    @PreferenceKey("age")
    open var age: Int = 24
}