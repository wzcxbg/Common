package com.sliver.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 只支持竖向的树形布局
 */
class CustomTreeLayout : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val treeWidth = convertDpToPx(32f)          //树的宽度
    private val treeRootOffset = treeWidth / 2              //树根偏移量
    private val treeTrunkCubicLen = convertDpToPx(8f)   //树干的贝塞尔曲线长度
    private val treeBranchCubicLen = convertDpToPx(8f)  //树枝的贝塞尔曲线长度
    private val paint = Paint()
    private val path = Path()

    init {
        paint.strokeCap = Paint.Cap.BUTT
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true

        //目前只支持竖向，因此orientation、gravity设为固定值
        //等适配了其他方向，再解除限制
        orientation = VERTICAL
        gravity = Gravity.END
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)

        if (modeWidth == MeasureSpec.AT_MOST) {
            val adjustedWidthSpec = MeasureSpec.makeMeasureSpec(
                sizeWidth - treeWidth, MeasureSpec.AT_MOST
            )
            super.onMeasure(adjustedWidthSpec, heightMeasureSpec)
            setMeasuredDimension(min(measuredWidth + treeWidth, sizeWidth), measuredHeight)
        } else if (modeWidth == MeasureSpec.EXACTLY) {
            val adjustedWidthSpec = MeasureSpec.makeMeasureSpec(
                sizeWidth - treeWidth, MeasureSpec.EXACTLY
            )
            super.onMeasure(adjustedWidthSpec, heightMeasureSpec)
            setMeasuredDimension(min(measuredWidth + treeWidth, sizeWidth), measuredHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        //画树干，仅支持从上往下延申的树干
        if (childCount > 0) {
            var childHeightMin = measuredHeight
            var childHeightMax = 0
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childCenterY = (child.top + child.bottom) / 2
                if (childCenterY < childHeightMin) {
                    childHeightMin = childCenterY
                }
                if (childCenterY > childHeightMax) {
                    childHeightMax = childCenterY
                }
            }
            canvas.drawLine(
                treeRootOffset.toFloat(), paddingTop.toFloat(),
                treeRootOffset.toFloat(), (childHeightMax - treeTrunkCubicLen).toFloat(),
                paint,
            )
        }

        //画树枝
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val treeBranchY = (childView.top + childView.bottom) / 2
            val treeBranchStart = treeRootOffset
            val treeBranchEnd = childView.left

            path.reset()
            path.moveTo(treeBranchStart.toFloat(), (treeBranchY - treeTrunkCubicLen).toFloat())
            path.quadTo(
                treeBranchStart.toFloat(), treeBranchY.toFloat(),
                (treeBranchStart + treeBranchCubicLen).toFloat(), treeBranchY.toFloat()
            )
            path.lineTo(treeBranchEnd.toFloat(), treeBranchY.toFloat())

            canvas.drawPath(path, paint)
        }

        //画子项
        super.onDraw(canvas)
    }

    private fun convertDpToPx(dp: Float): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).roundToInt()
    }
}