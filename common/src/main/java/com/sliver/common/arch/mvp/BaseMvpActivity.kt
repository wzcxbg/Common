package com.sliver.common.arch.mvp

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.sliver.common.base.BaseActivity
import java.lang.reflect.ParameterizedType

open class BaseMvpActivity<B : ViewBinding, V : IView, P : IPresenter<V>> : BaseActivity<B>() {
    protected lateinit var presenter: IPresenter<V>
    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = createPresenter()
        presenter.onAttach(this as V)
        super.onCreate(savedInstanceState)
    }

    private fun createPresenter(): P {
        val superClass = this.javaClass.genericSuperclass as ParameterizedType
        val modelClass = superClass.actualTypeArguments[1] as Class<*>
        val constructor = modelClass.getConstructor()
        constructor.isAccessible = true
        return constructor.newInstance() as P
    }
}