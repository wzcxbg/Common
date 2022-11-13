package com.sliver.common.preference

import com.sliver.common.preference.annotation.PreferenceKey
import com.sliver.common.preference.annotation.PreferenceName

@PreferenceName("user")
interface Preference {
    @PreferenceKey("username")
    var username: String

    @PreferenceKey("password")
    var password: String

    @PreferenceKey("age")
    var age: Int
}
