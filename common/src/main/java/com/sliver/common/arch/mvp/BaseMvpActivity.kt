package com.sliver.common.arch.mvp

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.sliver.common.base.BaseActivity

/**
 * 自动为全局变量内BasePresenter的子类使用空参构造函数创建并初始化
 */
open class BaseMvpActivity<B : ViewBinding> : BaseActivity<B>(), IView {
    protected inline fun <reified T : BasePresenter<*>> presenters(): Lazy<T> {
        val constructor = T::class.java.getConstructor()
        val presenter = constructor.newInstance()
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                presenter as BasePresenter<IView>
                presenter.onAttach(this@BaseMvpActivity)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                presenter.onDetach()
            }
        })
        return lazyOf(presenter)
    }
}