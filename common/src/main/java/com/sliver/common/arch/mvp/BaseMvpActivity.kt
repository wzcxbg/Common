package com.sliver.common.arch.mvp

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.sliver.common.base.BaseActivity

/**
 * 自动为全局变量内BasePresenter的子类使用空参构造函数创建并初始化
 */
open class BaseMvpActivity<B : ViewBinding> : BaseActivity<B>(), IView {
    private val presenters = ArrayList<BasePresenter<IView>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        presenters.addAll(createPresenters())
        for (presenter in presenters) {
            presenter.onAttach(this)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        for (presenter in presenters) {
            presenter.onDetach()
        }
        presenters.clear()
    }

    private fun createPresenters(): List<BasePresenter<IView>> {
        val presenters = ArrayList<BasePresenter<IView>>()
        val declaredFields = this.javaClass.declaredFields
        for (declaredField in declaredFields) {
            declaredField.isAccessible = true
            val fieldClass = declaredField.type
            val assignableFromBasePresenter = BasePresenter::class.java.isAssignableFrom(fieldClass)
            if (!assignableFromBasePresenter) continue
            val presenter = createPresenter(fieldClass as Class<out BasePresenter<IView>>)
            declaredField.set(this, presenter)
            presenters.add(presenter)
        }
        return presenters
    }

    private fun createPresenter(fieldClass: Class<out BasePresenter<IView>>): BasePresenter<IView> {
        val constructor = fieldClass.getConstructor()
        return constructor.newInstance()
    }
}