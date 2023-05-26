package com.sliver.common.login.mvvm

import android.app.ProgressDialog
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sliver.common.base.BaseActivity
import com.sliver.common.databinding.ActivityLoginBinding
import com.sliver.common.login.mvvm.LoginViewModel.LoginEvent.LoginFailed
import com.sliver.common.login.mvvm.LoginViewModel.LoginEvent.LoginSuccess
import com.sliver.common.login.mvvm.LoginViewModel.LoginIntent.Login
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel by viewModels<LoginViewModel>()
    private val dialog by lazy { ProgressDialog(this) }

    override fun initView() {
        viewModel.state
            .onEach { bindLoginState(it) }
            .launchIn(lifecycleScope)

        viewModel.event
            .onEach { handleLoginEvent(it) }
            .launchIn(lifecycleScope)
    }

    override fun initListener() {
        binding.login.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            viewModel.emitIntent(Login(username, password))
        }
    }

    private fun bindLoginState(state: LoginViewModel.LoginState) {
        if (state.isLoading) {
            dialog.show()
        } else {
            dialog.dismiss()
        }
    }

    private fun handleLoginEvent(event: LoginViewModel.LoginEvent) {
        when (event) {
            is LoginFailed -> showLoginFailedReason(event.reason)
            is LoginSuccess -> launchNextActivity()
        }
    }

    private fun showLoginFailedReason(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun launchNextActivity() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
    }
}