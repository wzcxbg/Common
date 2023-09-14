package com.sliver.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import kotlin.math.roundToInt

/**
 * 局限：带泛型的类及其子类不能混淆
 * 反射的泛型类不能混淆
 */

open class CustomDialog<T : ViewBinding>(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val superClass = this.javaClass.genericSuperclass as ParameterizedType
        val bindingClass = superClass.actualTypeArguments[0] as Class<*>
        val method = bindingClass.getMethod("inflate", LayoutInflater::class.java)

        val binding = method.invoke(null, layoutInflater) as T
        bindViews(binding, this)
        setContentView(binding.root)

        val builder = buildParams()
        builder?.customView(null)
        builder?.applyParameter(this)
    }

    protected open fun bindViews(binding: T, dialog: Dialog) {

    }

    protected open fun buildParams(): Builder? {
        return null
    }


    class Builder(private val context: Context) {
        private var view: View? = null
        private var gray = 0.5f
        private var width = WindowManager.LayoutParams.WRAP_CONTENT
        private var height = WindowManager.LayoutParams.WRAP_CONTENT
        private var gravity = Gravity.CENTER
        private var anime = android.R.style.Animation_Dialog
        private var cancelable = true
        private var padding = RectF(0f, 0f, 0f, 0f)

        fun customView(view: View?) = apply { this.view = view }
        fun backgroundGray(gray: Float) = apply { this.gray = gray }
        fun width(width: Int) = apply { this.width = width }
        fun height(height: Int) = apply { this.height = height }
        fun gravity(gravity: Int) = apply { this.gravity = gravity }
        fun animate(animateId: Int) = apply { this.anime = animateId }
        fun cancelable(cancelAble: Boolean) = apply { this.cancelable = cancelAble }
        fun padding(padding: Float) = apply { this.padding = RectF(padding, padding, padding, padding) }
        fun padding(left: Float, top: Float, right: Float, bottom: Float) =
            apply { this.padding = RectF(left, top, right, bottom) }


        fun applyParameter(dialog: Dialog): Dialog {
            val window = dialog.window
            val decorView = window?.decorView
            val attributes = window?.attributes

            val displayMetrics = context.resources.displayMetrics
            decorView?.setPadding(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.left, displayMetrics).roundToInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.top, displayMetrics).roundToInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.right, displayMetrics).roundToInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.bottom, displayMetrics).roundToInt(),
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window?.setWindowAnimations(anime)
            window?.setGravity(gravity)
            window?.setDimAmount(gray)

            attributes?.width = width
            attributes?.height = height

            dialog.setCanceledOnTouchOutside(cancelable)
            dialog.setCancelable(cancelable)
            dialog.setContentView(view ?: return dialog)
            return dialog
        }

        fun build(): Dialog {
            val dialog = Dialog(context)
            return applyParameter(dialog)
        }
    }


    class BindingBuilder<T : ViewBinding>(private val binding: T) {
        private val context = binding.root.context
        private val builder = Builder(context)
        private var listener: ((binding: T, dialog: Dialog) -> Unit)? = null

        fun bindView(listener: T.(Dialog) -> Unit) = apply { this.listener = listener }
        fun backgroundGray(gray: Float) = apply { builder.backgroundGray(gray) }
        fun width(width: Int) = apply { builder.width(width) }
        fun height(height: Int) = apply { builder.height(height) }
        fun gravity(gravity: Int) = apply { builder.gravity(gravity) }
        fun animate(animateId: Int) = apply { builder.animate(animateId) }
        fun cancelable(cancelAble: Boolean) = apply { builder.cancelable(cancelAble) }
        fun padding(padding: Float) = apply { builder.padding(padding) }
        fun padding(left: Float, top: Float, right: Float, bottom: Float) =
            apply { builder.padding(left, top, right, bottom) }

        fun applyParameter(dialog: Dialog): Dialog {
            builder.customView(binding.root)
            builder.applyParameter(dialog)
            listener?.invoke(binding, dialog)
            return dialog
        }

        fun build(): Dialog {
            val dialog = Dialog(context)
            return applyParameter(dialog)
        }
    }

    companion object {
        inline fun <reified T : ViewBinding> builder(context: Context): BindingBuilder<T> {
            val inflate = T::class.java.getMethod("inflate", LayoutInflater::class.java)
            val binding = inflate.invoke(inflate, LayoutInflater.from(context)) as T
            return BindingBuilder(binding)
        }
    }
}