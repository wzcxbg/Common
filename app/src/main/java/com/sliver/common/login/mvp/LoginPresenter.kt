package com.sliver.common.login.mvp

import com.sliver.common.arch.mvp.BasePresenter

class LoginPresenter : BasePresenter<LoginContract.View>(), LoginContract.Presenter {
    private val loginModel by models<LoginModel>()

    override fun login(username: String, password: String) {
        view.showLoadingDialog()
        loginModel.login(username, password, object : LoginContract.Model.LoginCallback {
            override fun onSuccess() {
                view.hideLoadingDialog()
                view.launchNextActivity()
            }

            override fun onFailed() {
                view.hideLoadingDialog()
                view.showLoginFailedReason("用户名或密码错误")
            }
        })
    }
}