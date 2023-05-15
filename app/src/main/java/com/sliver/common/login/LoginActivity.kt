package com.sliver.common.login

import android.app.ProgressDialog
import android.widget.Toast
import com.sliver.common.arch.mvp.BaseMvpActivity
import com.sliver.common.databinding.ActivityLoginBinding

/**
 * 当前设计的MVP的不足：不够规范，
 * 可能存在需要在Presenter层获取Context的情况
 * 可能存在需要在Model层创建Repository的情况
 */
class LoginActivity : BaseMvpActivity<ActivityLoginBinding>(), LoginContract.View {
    private val presenter by presenters<LoginPresenter>()
    private lateinit var dialog: ProgressDialog

    override fun initData() {
        dialog = ProgressDialog(this)
    }

    override fun initListener() {
        binding.login.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()
            presenter.login(username, password)
        }
    }

    override fun showLoadingDialog() {
        dialog.show()
    }

    override fun hideLoadingDialog() {
        dialog.dismiss()
    }

    override fun showLoginFailedReason(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun launchNextActivity() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
    }
}