package com.sliver.common.login

import com.sliver.common.arch.mvp.IModel
import com.sliver.common.arch.mvp.IPresenter
import com.sliver.common.arch.mvp.IView

interface LoginContract {
    interface Model : IModel {

    }

    interface View : IView {

    }

    interface Presenter : IPresenter<View> {

    }
}