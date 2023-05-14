package com.sliver.common.login

import com.sliver.common.arch.mvp.BaseModel

class LoginModel : BaseModel(), LoginContract.Model {
    override fun login(username: String, password: String, callback: LoginContract.Model.LoginCallback) {
        if (username == "wzcxbg" && password == "123456") {
            callback.onSuccess()
        } else {
            callback.onFailed()
        }
    }
}