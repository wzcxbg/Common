package com.sliver.common.arch.mvp

import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType


open class BasePresenter<V : IView, M : IModel> : IPresenter<V> {
    private var viewReference: WeakReference<V>? = null
    private var modelReference: WeakReference<M>? = null
    protected val view: V get() = requireNotNull(viewReference?.get())
    protected val model: M get() = requireNotNull(modelReference?.get())

    override fun onAttach(view: V) {
        this.viewReference = WeakReference(view)
        this.modelReference = WeakReference(createModel())
    }

    override fun onDetach() {
        this.viewReference?.clear()
        this.viewReference = null
        this.modelReference?.clear()
        this.modelReference = null
    }

    private fun createModel(): M {
        val superClass = this.javaClass.genericSuperclass as ParameterizedType
        val modelClass = superClass.actualTypeArguments[1] as Class<*>
        val constructor = modelClass.getConstructor()
        constructor.isAccessible = true
        return constructor.newInstance() as M
    }
}