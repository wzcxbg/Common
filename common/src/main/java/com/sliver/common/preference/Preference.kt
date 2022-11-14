package com.sliver.common.preference

import com.sliver.config_annotation.PreferenceKey
import com.sliver.config_annotation.PreferenceName

@PreferenceName("user")
interface Preference {
    @PreferenceKey("username")
    var username: String

    @PreferenceKey("password")
    var password: String

    @PreferenceKey("age")
    var age: Int
}
