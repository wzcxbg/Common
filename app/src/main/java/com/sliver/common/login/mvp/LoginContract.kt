package com.sliver.common.login.mvp

import com.sliver.common.arch.mvp.IModel
import com.sliver.common.arch.mvp.IPresenter
import com.sliver.common.arch.mvp.IView

interface LoginContract {
    interface Model : IModel {
        fun login(username: String, password: String, callback: LoginCallback)

        interface LoginCallback {
            fun onSuccess()
            fun onFailed()
        }
    }

    interface View : IView {
        fun showLoadingDialog()
        fun hideLoadingDialog()
        fun showLoginFailedReason(msg: String)
        fun launchNextActivity()
    }

    interface Presenter : IPresenter<View> {
        fun login(username: String, password: String)
    }
}