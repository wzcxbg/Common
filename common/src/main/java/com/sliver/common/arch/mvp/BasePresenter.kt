package com.sliver.common.arch.mvp

import java.lang.ref.WeakReference

/**
 * 自动为全局变量内BaseModel的子类使用空参构造函数创建并初始化
 */
open class BasePresenter<V : IView> : IPresenter<V> {
    private var viewReference: WeakReference<V>? = null
    protected val view: V get() = requireNotNull(viewReference?.get())
    private val models = ArrayList<BaseModel>()

    override fun onAttach(view: V) {
        this.viewReference = WeakReference(view)
        this.models.addAll(createModels())
    }

    override fun onDetach() {
        this.viewReference?.clear()
        this.viewReference = null
        this.models.clear()
    }

    private fun createModels(): List<BaseModel> {
        val models = ArrayList<BaseModel>()
        val declaredFields = this.javaClass.declaredFields
        for (declaredField in declaredFields) {
            declaredField.isAccessible = true
            val fieldClass = declaredField.type
            val assignableFromBaseModel = BaseModel::class.java.isAssignableFrom(fieldClass)
            if (!assignableFromBaseModel) continue
            val model = createModel(fieldClass as Class<out BaseModel>)
            declaredField.set(this, model)
            models.add(model)
        }
        return models
    }

    private fun createModel(fieldClass: Class<out BaseModel>): BaseModel {
        val constructor = fieldClass.getConstructor()
        return constructor.newInstance()
    }
}