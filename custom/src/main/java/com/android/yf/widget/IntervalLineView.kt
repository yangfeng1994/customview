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
        canvas?.drawRoundRect(2F, 1F, 48F, 3F, 4F, 4F, mBackGroundPaint)
        val progressWidth = progress.times(46F).div(100)
        canvas?.drawRoundRect(2F, 1F, progressWidth.plus(2), 3F, 4F, 4F, mPaint)
        if (drawPoint) {
            canvas?.drawCircle(progressWidth.plus(2), 2F, 2F, mPaint)
        }
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        postInvalidate()
    }

}