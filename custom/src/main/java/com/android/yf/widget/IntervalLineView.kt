package com.android.yf.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * 绘制区间的view
 */
class IntervalLineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {


    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    private val mBackGroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    /**
     *小圆点的半径
     */
    var thumbRadius: Float = 0F

    var progressHeight = 0F

    init {
        obtain(context, attrs)
    }

    private fun obtain(context: Context, attrs: AttributeSet? = null) {
        val type = context.obtainStyledAttributes(attrs, R.styleable.IntervalLineView)
        mPaint.color = type.getColor(
            R.styleable.IntervalLineView_inter_line_progress_color,
            Color.parseColor("#64BC1C")
        )
        mBackGroundPaint.color = type.getColor(
            R.styleable.IntervalLineView_inter_line_progress_background_color,
            Color.parseColor("#33FFFFFF")
        )
        thumbRadius = type.getDimension(
            R.styleable.IntervalLineView_inter_line_progress_thumb_radius,
            resources.getDimension(R.dimen.d_1)
        )
        progressHeight = type.getDimension(
            R.styleable.IntervalLineView_inter_line_progress_height,
            resources.getDimension(R.dimen.d_1)
        )
    }

    private var progress: Int = 0
    var drawPoint = false
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawProgress(canvas)
    }

    /**
     * 绘制进度
     */
    private fun drawProgress(canvas: Canvas?) {
        val rx = progressHeight.div(2)
        val left = thumbRadius
        val top = height.minus(progressHeight).div(2)
        val right = width.minus(left)
        val bottom = top.plus(progressHeight)
        val width = right.minus(left)
        canvas?.drawRoundRect(left, top, right, bottom, rx, rx, mBackGroundPaint)
        val progressWidth = progress.times(width).div(100).plus(left)
        canvas?.drawRoundRect(left, top, progressWidth, bottom, rx, rx, mPaint)
        if (drawPoint) {
            canvas?.drawCircle(
                progressWidth,
                height.toFloat().div(2),
                thumbRadius,
                mPaint
            )
        }
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        postInvalidate()
    }

}