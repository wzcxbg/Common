package com.sliver.common.login.mvvm

import androidx.lifecycle.viewModelScope
import com.sliver.common.BaseViewModel
import com.sliver.common.login.mvvm.LoginViewModel.LoginIntent.Login
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel : BaseViewModel<
        LoginViewModel.LoginState,
        LoginViewModel.LoginEvent,
        LoginViewModel.LoginIntent>() {

    init {
        intent
            .onEach { handleIntent(it) }
            .launchIn(viewModelScope)
    }

    data class LoginState(
        val isLoading: Boolean = false,
    ) : State()

    sealed class LoginEvent : Event() {
        class LoginFailed(val reason: String) : LoginEvent()
        class LoginSuccess(val username: String) : LoginEvent()
    }

    sealed class LoginIntent : Intent() {
        class Login(val username: String, val password: String) : LoginIntent()
    }

    private fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is Login -> doLogin(intent.username, intent.password)
        }
    }

    private fun doLogin(username: String, password: String) {
        tryEmit(
            state.value.copy(
                isLoading = true
            )
        )
        if (username == "wzcxbg" && password == "123456") {
            tryEmit(LoginEvent.LoginSuccess(username))
        } else {
            tryEmit(LoginEvent.LoginFailed("用户名或密码不正确"))
        }

        tryEmit(
            state.value.copy(
                isLoading = false
            )
        )
    }
}