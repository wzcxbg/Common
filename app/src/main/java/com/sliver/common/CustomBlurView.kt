package com.emeet.ows

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView
import com.emeet.ows.custom.CustomRealtimeBlurView

class CustomBlurView : AppCompatImageView, ViewTreeObserver.OnPreDrawListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val decorView by lazy { getActivityDecorView() }
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var listener: ((Bitmap) -> Unit)? = null
    private val blurKit = CustomRealtimeBlurView.AndroidStockBlurImpl()
    private var outBitmap: Bitmap? = null
    private var drawBitmap = true

    fun setBitmapListener(listener: (Bitmap) -> Unit) {
        this.listener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap?.recycle()
        outBitmap?.recycle()
        val newBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        val newCanvas = Canvas(newBitmap)
        blurKit.prepare(context, newBitmap, 24f)
        bitmap = newBitmap
        canvas = newCanvas
        outBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        decorView?.viewTreeObserver?.addOnPreDrawListener(this)
    }

    override fun onPreDraw(): Boolean {
        val locations = IntArray(2)
        getLocationInWindow(locations)
        val left = locations[0]
        val top = locations[1]
        canvas?.save()
        canvas?.translate(+left.toFloat(), -top.toFloat())
        drawBitmap = false
        decorView?.draw(canvas)
        drawBitmap = true
        canvas?.restore()
        blurKit.blur(bitmap, outBitmap)
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        if (drawBitmap) {
            canvas?.drawBitmap(
                outBitmap ?: return,
                0f, 0f, null
            )
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        decorView?.viewTreeObserver?.removeOnPreDrawListener(this)
    }

    private fun getActivityDecorView(): View? {
        var ctx: Context? = context
        for (i in 0 until 4) {
            if (ctx is Activity) break
            if (ctx !is ContextWrapper) break
            ctx = ctx.baseContext
        }
        if (ctx is Activity) {
            return ctx.window.decorView
        }
        return null
    }
}