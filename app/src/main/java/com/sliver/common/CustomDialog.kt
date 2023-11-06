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
inline fun <reified T : CustomDialog<out ViewBinding>> Activity.dialogs(): Lazy<T> {
    val constructor = T::class.java.getConstructor(Context::class.java)
    return lazy(LazyThreadSafetyMode.NONE) { constructor.newInstance(this) }
}

inline fun <reified T : CustomDialog<out ViewBinding>> Fragment.dialogs(): Lazy<T> {
    val constructor = T::class.java.getConstructor(Context::class.java)
    return lazy(LazyThreadSafetyMode.NONE) { constructor.newInstance(requireActivity()) }
}

open class CustomDialog<T : ViewBinding>(context: Context) : ComponentDialog(context) {
    protected val builder = Builder<T>(context)
    protected val binding = createBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder.customView(binding.root)
        initView(builder)
        builder.applyParameter(this)
    }

    open fun initView(builder: Builder<T>) {

    }

    private fun createBinding(): T {
        val superClass = this.javaClass.genericSuperclass as ParameterizedType
        val bindingClass = superClass.actualTypeArguments[0] as Class<*>
        val method = bindingClass.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, layoutInflater) as T
    }

    class Builder<T : ViewBinding> {
        private val context: Context
        private var binding: T? = null

        private var view: View? = null
        private var gray = 0.5f
        private var width = WindowManager.LayoutParams.WRAP_CONTENT
        private var height = WindowManager.LayoutParams.WRAP_CONTENT
        private var gravity = Gravity.CENTER
        private var anime = android.R.style.Animation_Dialog
        private var cancelable = true
        private var padding = RectF(0f, 0f, 0f, 0f)
        private var listener: ((binding: T, dialog: Dialog) -> Unit)? = null

        constructor(context: Context) {
            this.context = context
            this.binding = null
        }

        constructor(binding: T) {
            this.context = binding.root.context
            this.binding = binding
        }

        fun customView(view: View?) = apply { this.view = view }
        fun customView(binding: T?) = apply { this.binding = binding }
        fun customView(clazz: Class<T>) = apply { this.binding = create(clazz) }
        fun applyBinding(listener: T.(Dialog) -> Unit) = apply { this.listener = listener }
        fun backgroundGray(@DimAmountRange gray: Float) = apply { this.gray = gray }
        fun width(@LayoutParamFlags width: Int) = apply { this.width = width }
        fun height(@LayoutParamFlags height: Int) = apply { this.height = height }
        fun gravity(@GravityFlags gravity: Int) = apply { this.gravity = gravity }
        fun animate(@AnimationFlags animateId: Int) = apply { this.anime = animateId }
        fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun padding(
            @Dimension(unit = Dimension.DP) padding: Float
        ) = apply { this.padding = RectF(padding, padding, padding, padding) }

        fun padding(
            @Dimension(unit = Dimension.DP) left: Float,
            @Dimension(unit = Dimension.DP) top: Float,
            @Dimension(unit = Dimension.DP) right: Float,
            @Dimension(unit = Dimension.DP) bottom: Float
        ) = apply { this.padding = RectF(left, top, right, bottom) }

        fun applyParameter(dialog: Dialog): Dialog {
            val window = dialog.window
            val decorView = window?.decorView
            val attributes = window?.attributes

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //TODO 支持沉浸式状态栏
            //window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window?.setWindowAnimations(anime)
            window?.setGravity(gravity)
            window?.setDimAmount(gray)

            attributes?.width = width
            attributes?.height = height

            dialog.setCanceledOnTouchOutside(cancelable)
            dialog.setCancelable(cancelable)
            dialog.setContentView(view ?: binding?.root ?: return dialog)

            val displayMetrics = context.resources.displayMetrics
            decorView?.setPadding(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.left, displayMetrics).roundToInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.top, displayMetrics).roundToInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.right, displayMetrics).roundToInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding.bottom, displayMetrics).roundToInt(),
            )

            listener?.invoke(binding ?: return dialog, dialog)
            return dialog
        }

        fun build(): Dialog {
            val dialog = Dialog(context)
            return applyParameter(dialog)
        }

        private fun <T : ViewBinding> create(clazz: Class<T>): T {
            val inflate = clazz.getMethod("inflate", LayoutInflater::class.java)
            val binding = inflate.invoke(inflate, LayoutInflater.from(context))
            return binding as T
        }

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(
            flag = false,
            open = false,
            value = [
                Gravity.FILL,
                Gravity.FILL_HORIZONTAL,
                Gravity.FILL_VERTICAL,
                Gravity.START,
                Gravity.END,
                Gravity.LEFT,
                Gravity.RIGHT,
                Gravity.TOP,
                Gravity.BOTTOM,
                Gravity.CENTER,
                Gravity.CENTER_HORIZONTAL,
                Gravity.CENTER_VERTICAL,
                Gravity.DISPLAY_CLIP_HORIZONTAL,
                Gravity.DISPLAY_CLIP_VERTICAL,
                Gravity.CLIP_HORIZONTAL,
                Gravity.CLIP_VERTICAL,
                Gravity.NO_GRAVITY
            ]
        )
        annotation class GravityFlags

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(
            flag = false,
            open = true,
            value = [
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.MATCH_PARENT,
            ]
        )
        annotation class LayoutParamFlags

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(
            flag = false,
            open = true,
            value = [
                android.R.style.Animation_Dialog,
                android.R.style.Animation_InputMethod,
                android.R.style.Animation_Toast,
                android.R.style.Animation_Translucent,
            ]
        )
        annotation class AnimationFlags

        @Retention(AnnotationRetention.SOURCE)
        @FloatRange(
            from = 0.0,
            to = 1.0,
            fromInclusive = true,
            toInclusive = true,
        )
        annotation class DimAmountRange
    }
}
