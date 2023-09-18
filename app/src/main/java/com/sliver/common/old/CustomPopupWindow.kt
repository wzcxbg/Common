package com.emeet.multicast.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.transition.Transition
import android.util.DisplayMetrics
import android.util.SparseArray
import android.util.TypedValue
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.math.roundToInt

open class CustomPopupWindow<T : ViewBinding>(context: Context) : PopupWindow(context) {
    lateinit var binding: T
    private val dismissViews = ArrayList<View>()
    private val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            for (dismissView in dismissViews) {
                if (e.x > dismissView.left && e.x < dismissView.right && e.y > dismissView.top && e.y < dismissView.bottom) {
                    dismiss()
                    return super.onSingleTapUp(e)
                }
            }
            return super.onSingleTapUp(e)
        }
    })

    @SuppressLint("ClickableViewAccessibility")
    private val touchEvent = View.OnTouchListener { _, event ->
        detector.onTouchEvent(event)
        false
    }

    init {
        //setTouchInterceptor(touchEvent)
    }

    fun addDismissViews(vararg dismissViews: View) {
        this.dismissViews.addAll(dismissViews)
    }

    class PopupConstrainsLocator(private val popupWindow: PopupWindow) {
        private val lp = ConstraintLayout.LayoutParams(0, 0)
        fun startToStart() = apply { lp.startToStart = 1 }
        fun startToEnd() = apply { lp.startToEnd = 1 }
        fun endToStart() = apply { lp.endToStart = 1 }
        fun endToEnd() = apply { lp.endToEnd = 1 }
        fun topToTop() = apply { lp.topToTop = 1 }
        fun topToBottom() = apply { lp.topToBottom = 1 }
        fun bottomToTop() = apply { lp.bottomToTop = 1 }
        fun bottomToBottom() = apply { lp.bottomToBottom = 1 }
        fun marginStart(margin: Float) = apply { lp.setMargins(dpToPx(margin), lp.topMargin, lp.rightMargin, lp.bottomMargin) }
        fun marginEnd(margin: Float) = apply { lp.setMargins(lp.leftMargin, lp.topMargin, dpToPx(margin), lp.bottomMargin) }
        fun marginTop(margin: Float) = apply { lp.setMargins(lp.leftMargin, dpToPx(margin), lp.rightMargin, lp.bottomMargin) }
        fun marginBottom(margin: Float) = apply { lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, dpToPx(margin)) }

        /**
         * 计算PopupWindow(src)在锚点控件(dst)上的位置
         */
        fun showAsDropDown(anchor: View) {
            val location = IntArray(2)
            anchor.getLocationOnScreen(location)
            popupWindow.contentView.measure(0, 0)

            val (dstX, dstY) = location[0] to location[1]
            val (dstW, dstH) = anchor.measuredWidth to anchor.measuredHeight
            val (srcW, srcH) = popupWindow.contentView.measuredWidth to popupWindow.contentView.measuredHeight
            var (srcX, srcY) = 0 to 0

            //计算x坐标
            var (xLeftEdge, xRightEdge) = 0 to 0
            if (lp.startToStart == 1 && lp.startToEnd == 1) {
                xLeftEdge = dstW / 2
            } else if (lp.startToStart == 1) {
                xLeftEdge = 0
            } else if (lp.startToEnd == 1) {
                xLeftEdge = dstW
            }
            if (lp.endToEnd == 1 && lp.endToStart == 1) {
                xRightEdge = (dstW / 2) - srcW
            } else if (lp.endToStart == 1) {
                xRightEdge = -srcW
            } else if (lp.endToEnd == 1) {
                xRightEdge = dstW - srcW
            }
            if ((lp.startToStart == 1 || lp.startToEnd == 1)
                && (lp.endToStart == 1 || lp.endToEnd == 1)
            ) {
                srcX = dstX + ((xLeftEdge + lp.leftMargin) + (xRightEdge - lp.rightMargin)) / 2
            } else if (lp.startToStart == 1 || lp.startToEnd == 1) {
                srcX = dstX + (xLeftEdge + lp.leftMargin)
            } else if (lp.endToStart == 1 || lp.endToEnd == 1) {
                srcX = dstX + (xRightEdge - lp.rightMargin)
            }

            //计算y坐标
            var (yTopEdge, yBottomEdge) = 0 to 0
            if (lp.topToTop == 1 && lp.topToBottom == 1) {
                yTopEdge = dstH / 2
            } else if (lp.topToTop == 1) {
                yTopEdge = 0
            } else if (lp.topToBottom == 1) {
                yTopEdge = dstH
            }
            if (lp.bottomToTop == 1 && lp.bottomToBottom == 1) {
                yBottomEdge = dstH / 2 - srcH
            } else if (lp.bottomToTop == 1) {
                yBottomEdge = -srcH
            } else if (lp.bottomToBottom == 1) {
                yBottomEdge = dstH - srcH
            }
            if ((lp.topToTop == 1 || lp.topToBottom == 1) &&
                (lp.bottomToTop == 1 || lp.bottomToBottom == 1)
            ) {
                srcY = dstY + ((yTopEdge + lp.topMargin) + (yBottomEdge - lp.bottomMargin)) / 2
            } else if (lp.topToTop == 1 || lp.topToBottom == 1) {
                srcY = dstY + (yTopEdge + lp.topMargin)
            } else if (lp.bottomToTop == 1 || lp.bottomToBottom == 1) {
                srcY = dstY + (yBottomEdge - lp.bottomMargin)
            }

            popupWindow.showAtLocation(anchor, 0, srcX, srcY)
        }

        private fun dpToPx(dp: Float): Int {
            val context = popupWindow.contentView.context
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            val result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            return result.roundToInt()
        }
    }

    /**
     * 使用Locator定位并显示
     * 仅支持contextView与popupWindow大小相同的情况
     * (popupWindow.contentView.measureWidth、popupWindow.width)
     * (popupWindow.contentView.measureHeight、popupWindow.height)
     */
    fun withConstrains(): PopupConstrainsLocator {
        return PopupConstrainsLocator(this)
    }

    companion object {
        //全局缓存的PopupWindow
        val persistMap = HashMap<String, CustomPopupWindow<*>>()
        inline fun <reified T : ViewBinding> Builder(context: Context): BindingBuilder<T> {
            return BindingBuilder(context, T::class.java)
        }

        fun <T : ViewBinding> fromPersistence(persistTag: String?): CustomPopupWindow<T>? {
            @Suppress("UNCHECKED_CAST")
            return persistMap[persistTag] as CustomPopupWindow<T>?
        }
    }

    open class BindingBuilder<T : ViewBinding>(private val context: Context, bindingClass: Class<T>) {
        private lateinit var view: View
        private var width: Int
        private var height: Int
        private var anime: Int
        private var cancelable: Boolean
        private var enterTransition: Transition? = null
        private var exitTransition: Transition? = null
        private var persistTag: String? = null
        private var rebind = false
        private var lifecycleOwner: LifecycleOwner? = null
        private var dismissListener: () -> Unit

        private var idListeners = SparseArray<(View, View, PopupWindow) -> Unit>()
        private var dismissIds = ArrayList<Int>()

        init {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            anime = android.R.style.Animation_Dialog
            cancelable = true
            dismissListener = {}
        }

        fun customView(view: View) = apply { this.view = view }
        fun width(width: Int) = apply { this.width = width }
        fun width(width: Float) = apply { this.width = dp2px(context, width) }
        fun height(height: Int) = apply { this.height = height }
        fun height(height: Float) = apply { this.height = dp2px(context, height) }
        fun anime(anime: Int) = apply { this.anime = anime }
        fun anime(enterTransition: Transition?, exitTransition: Transition) = apply {
            this.enterTransition = enterTransition
            this.exitTransition = exitTransition
        }

        fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun persistWithLifecycle(persistTag: String? = null, owner: LifecycleOwner, rebind: Boolean = false) = apply {
            this.persistTag = persistTag ?: (owner.javaClass.name + binding.javaClass.name)
            this.rebind = rebind
            this.lifecycleOwner = owner
        }

        /**
         * 试验性
         * 退出弹窗时的事件
         */
        fun onDismiss(dismissListener: () -> Unit): BindingBuilder<T> {
            this.dismissListener = dismissListener
            return this
        }

        /**
         * 试验性
         * 当点击了某个Id时触发事件
         */
        fun case(@IdRes id: Int, listener: (view: View, contentView: View, dialog: PopupWindow) -> Unit): BindingBuilder<T> {
            idListeners.append(id, listener)
            return this
        }

        /**
         * 实验性
         * 点击时候退出的id
         */
        fun dismissId(@IdRes vararg dismissIds: Int): BindingBuilder<T> {
            for (dismissId in dismissIds) {
                this.dismissIds.add(dismissId)
            }
            return this
        }

        fun applyParameter(popupWindow: CustomPopupWindow<T>): CustomPopupWindow<T> {
            popupWindow.width = width
            popupWindow.height = height
            popupWindow.binding = binding
            popupWindow.contentView = binding.root

            popupWindow.animationStyle = anime
            popupWindow.enterTransition = enterTransition
            popupWindow.exitTransition = exitTransition
            popupWindow.isOutsideTouchable = cancelable
            popupWindow.isClippingEnabled = false
            //需要先设置背景elevation属性才有效
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow.elevation = 16f
            popupWindow.setOnDismissListener {
                dismissListener.invoke()
            }

            // 解决Android5.1及以下版本点击外部不消失问题
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            if (idListeners.size() != 0 || dismissIds.size != 0) {
                var addListener = { _: View, _: View.OnClickListener -> }
                addListener = { view: View, listener: View.OnClickListener ->
                    if (view.id != View.NO_ID) {
                        view.setOnClickListener(listener)
                    }
                    if (view is ViewGroup) {
                        val children = view.children
                        for (child in children) {
                            addListener(child, listener)
                        }
                    }
                }
                val onClickListener = View.OnClickListener {
                    val listener = idListeners[it.id]
                    listener?.invoke(it, view, popupWindow)
                    if (it.id in dismissIds) {
                        popupWindow.dismiss()
                    }
                }
                addListener(view, onClickListener)
            }

            /*ViewBinding*/
            onBind(popupWindow, binding)
            function.invoke(popupWindow, binding)

            return popupWindow
        }

        fun build(): CustomPopupWindow<T> {
            if (persistTag != null) {
                var popupWindow = persistMap[persistTag]
                @Suppress("UNCHECKED_CAST")
                if (popupWindow != null) {
                    if (rebind) {
                        function.invoke(popupWindow, popupWindow.binding as T)
                    }
                    return popupWindow as CustomPopupWindow<T>
                } else {
                    popupWindow = CustomPopupWindow<T>(binding.root.context)
                    applyParameter(popupWindow)
                    persistMap[persistTag!!] = popupWindow
                    lifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            persistMap.remove(persistTag!!)
                            popupWindow.dismiss()
                        }
                    })
                    lifecycleOwner = null
                    return popupWindow
                }
            }

            val popupWindow = CustomPopupWindow<T>(binding.root.context)
            applyParameter(popupWindow)
            return popupWindow
        }

        private val binding: T = createBinding(context, bindingClass)
        private var function: (popupWindow: PopupWindow, popupBinding: T) -> Unit = { _, _ -> }

        fun bind(function: (popupWindow: PopupWindow, popupBinding: T) -> Unit): BindingBuilder<T> {
            this.function = function
            return this
        }

        fun bindApply(function: T.(popupWindow: PopupWindow) -> Unit): BindingBuilder<T> {
            this.function = { popupWindow, popupBinding ->
                function.invoke(popupBinding, popupWindow)
            }
            return this
        }

        protected open fun onBind(popupWindow: PopupWindow, popupBinding: T) {

        }

        private fun createBinding(context: Context, bindingClass: Class<T>): T {
            val inflate = bindingClass.getMethod("inflate", LayoutInflater::class.java)
            val binding = inflate.invoke(inflate, LayoutInflater.from(context)) as T
            customView(binding.root)
            return binding
        }

        private fun dp2px(context: Context, dp: Float): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            return px.roundToInt()
        }
    }
}
