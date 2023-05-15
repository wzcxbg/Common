package com.sliver.common.arch.mvp

import java.lang.ref.WeakReference

/**
 * 自动为全局变量内BaseModel的子类使用空参构造函数创建并初始化
 */
open class BasePresenter<V : IView> : IPresenter<V> {
    private var viewReference: WeakReference<V>? = null
    protected val view: V get() = requireNotNull(viewReference?.get())

    override fun onAttach(view: V) {
        this.viewReference = WeakReference(view)
    }

    override fun onDetach() {
        this.viewReference?.clear()
        this.viewReference = null
    }

    protected inline fun <reified T : BaseModel> models(): Lazy<T> {
        return lazy<T> {
            val constructor = T::class.java.getConstructor()
            val model = constructor.newInstance()
            model
        }
    }
}