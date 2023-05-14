package com.sliver.common.login

import com.sliver.common.arch.mvp.BaseMvpActivity
import com.sliver.common.databinding.ActivityLoginBinding

class LoginActivity : BaseMvpActivity<ActivityLoginBinding, LoginContract.View, LoginPresenter>(), LoginContract.View {

    override fun initView() {
    }
}