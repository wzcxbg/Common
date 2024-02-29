package com.sliver.common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    protected val TAG = this::class.java.simpleName
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding(this)
        setContentView(binding.root)
        initData()
        initView()
        initListener()
    }

    protected open fun initData() {}
    protected open fun initView() {}
    protected open fun initListener() {}

    protected fun <T> argumentOf(name: String, default: T? = null): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy(LazyThreadSafetyMode.PUBLICATION) {
            //NullPointerException -> IllegalStateException
            intent?.extras ?: return@lazy default!!
            val result = try {
                intent.extras?.get(name) as T
            } catch (e: ClassCastException) {
                null
            }
            result ?: (default as T)
        }
    }

    private fun createBinding(context: Context): T {
        val superClass = this.javaClass.genericSuperclass as ParameterizedType
        val bindingClass = superClass.actualTypeArguments[0] as Class<*>
        val inflate = try {
            bindingClass.getMethod("inflate", LayoutInflater::class.java)
        } catch (e: Exception) {
            bindingClass.declaredMethods.first {
                val argumentTypes = arrayOf(LayoutInflater::class.java)
                it.parameterTypes.contentEquals(argumentTypes)
            }
        }
        inflate.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return inflate.invoke(null, LayoutInflater.from(context)) as T
    }
}