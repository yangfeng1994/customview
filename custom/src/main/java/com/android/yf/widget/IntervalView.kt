package com.android.yf.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

/**
 * 区间进度条
 */
class IntervalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    val mTypeface by lazy { Typeface.createFromAsset(context.assets, "Roboto-Regular.ttf") }

    val mPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = mTypeface } }

    private var mMinValue = 0
    private var mMaxValue = 0
    open var mCurrentValue = 0

    // 小圆点的半径
    private var mRadius = 1.5f

    /**
     * 是否开启auto模式
     */
    private var mIsOpenAuto = false


    // 画笔宽度
    private var mStrokeWidth = 2f
    private var mIsFirstOpenAuto = true
    private var mOnSetValue: (Int) -> Unit = {}

    /**
     * 默认字体颜色值
     */
    private var defaultValueTextColor = Color.parseColor("#ffffff")
    private var defaultProgressBarColor = Color.parseColor("#33FFFFFF")
    private var progressBarColor = Color.parseColor("#64BC1C")

    /**
     * 字体大小
     */
    private var mTextSize: Float = 18F

    /**
     * progress 距离左右边文字的边距
     */
    private var progressMargin: Float = 6F

    /**
     * 超出阈值时，绘制的左右边距
     */
    private var thresholdMargin: Float = 10F

    /**
     * 文字距离左边与右边的边距
     */
    private var valueTextMargin: Float = 4F

    /**
     * progressBar进度条的高度
     */
    private var progressBarHeight: Float = 2F

    init {
        mPaint.style = Paint.Style.FILL
        obtain(context, attrs)
    }

    private fun obtain(
        context: Context,
        attrs: AttributeSet? = null
    ) {
        val type = context.obtainStyledAttributes(attrs, R.styleable.IntervalView)
        mMinValue = type.getInt(R.styleable.IntervalView_interval_min_value, mMinValue)
        mMaxValue = type.getInt(R.styleable.IntervalView_interval_max_value, mMaxValue)
        mCurrentValue = type.getInt(R.styleable.IntervalView_interval_current_value, mCurrentValue)
        mRadius = type.getDimension(R.styleable.IntervalView_interval_point_radius_size, mRadius)
        mIsOpenAuto = type.getBoolean(R.styleable.IntervalView_interval_is_auto, mIsOpenAuto)
        mIsFirstOpenAuto =
            type.getBoolean(R.styleable.IntervalView_interval_first_auto, mIsFirstOpenAuto)
        mStrokeWidth =
            type.getDimension(R.styleable.IntervalView_interval_stroke_width, mStrokeWidth)
        mTextSize = type.getDimension(R.styleable.IntervalView_interval_text_size, mTextSize)
        valueTextMargin =
            type.getDimension(R.styleable.IntervalView_interval_value_margin, valueTextMargin)
        thresholdMargin =
            type.getDimension(R.styleable.IntervalView_interval_threshold_margin, thresholdMargin)
        progressMargin =
            type.getDimension(R.styleable.IntervalView_interval_progress_margin, progressMargin)
        progressBarHeight =
            type.getDimension(R.styleable.IntervalView_interval_progress_height, progressBarHeight)
        defaultValueTextColor = type.getColor(
            R.styleable.IntervalView_interval_default_value_text_color,
            defaultValueTextColor
        )
        progressBarColor =
            type.getColor(R.styleable.IntervalView_interval_progress_color, progressBarColor)
        defaultProgressBarColor = type.getColor(
            R.styleable.IntervalView_interval_default_progress_color,
            defaultProgressBarColor
        )
        progressBarHeight = progressBarHeight.div(2)
        mPaint.textSize = mTextSize
        mPaint.strokeWidth = mStrokeWidth
        val tf = Typeface.createFromAsset(context?.assets, "Roboto-Regular.ttf")
        mPaint.typeface = tf
        type?.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val drawDefaultProgress = drawDefaultProgress(canvas)
        drawProgress(canvas, drawDefaultProgress)
    }

    /**
     * 画默认的progress
     * margin  绘制 小于最小值，大于最大值 小圆点距离位置
     * starTextMargin 最小值text绘制的位置
     * progressStar progress 绘制开始的位置
     * maxTextMargin 绘制最大值开始的位置
     * progressEnd progress 绘制的结束位置
     */
    private fun drawDefaultProgress(canvas: Canvas?): FloatArray {
        val margin = thresholdMargin.plus(valueTextMargin)
        val minTextSize = mPaint?.measureText("$mMinValue")
        val starTextMargin = margin.plus(minTextSize)
        val progressStar = progressMargin.plus(starTextMargin)
        val maxMaxTextSize = mPaint?.measureText("$mMaxValue")
        val maxTextMargin = width.minus(margin.plus(mRadius)).minus(maxMaxTextSize)
        val progressEnd = maxTextMargin.minus(progressMargin)
        val axis = height.div(2F)
        mPaint.color = defaultProgressBarColor
        canvas?.drawRoundRect(
            progressStar,
            axis.minus(progressBarHeight),
            progressEnd,
            axis.plus(progressBarHeight),
            10F, 10F,
            mPaint
        )
        return floatArrayOf(margin, starTextMargin, progressStar, maxTextMargin, progressEnd)
    }

    /**
     * 绘制进度
     */
    private fun drawProgress(canvas: Canvas?, drawLocation: FloatArray) {
        //小圆点的颜色设置为绿色
        mPaint.color = progressBarColor
        when {
            //画最小值左边的圆点
            mCurrentValue < mMinValue -> {
                drawLessMin(canvas)
            }
            //画最大值右边的圆点
            mCurrentValue > mMaxValue -> {
                drawGreaterThanMax(canvas)
            }
            else -> {
                if (mIsOpenAuto) {
                    drawIntervalProgress(canvas, drawLocation)
                } else {
                    mPaint.color = defaultValueTextColor
                }
            }
        }
        drawMaxAndMinTex(canvas, drawLocation)
    }

    /**
     * 绘制区间值
     */
    private fun drawIntervalProgress(canvas: Canvas?, drawLocation: FloatArray) {
        val total = mMaxValue.minus(mMinValue)
        val currentPercent = mCurrentValue.minus(mMinValue).div(total.toFloat())
        val currentProgress = drawLocation[4].minus(drawLocation[2]).times(currentPercent)
        val progress = drawLocation[2].plus(currentProgress)
        // 实际实时进度，显示绿色进度条
        mPaint.color = progressBarColor
        val axis = height.div(2F)
        canvas?.drawRoundRect(
            drawLocation[2],
            axis.minus(progressBarHeight),
            progress,
            axis.plus(progressBarHeight),
            10F, 10F,
            mPaint
        )
        mPaint.color = progressBarColor
        canvas?.drawCircle(progress, height / 2f, mRadius, mPaint)
        if (mMinValue == 0 && mMaxValue == 0) {
            mPaint.color = defaultValueTextColor
        }
    }

    /**
     * 绘制小于最小值
     */
    private fun drawLessMin(canvas: Canvas?) {
        canvas?.drawCircle(thresholdMargin.minus(mRadius.div(2)), height / 2f, mRadius, mPaint)
        mPaint.color = defaultValueTextColor
    }

    /**
     * 绘制大于最大值
     */
    private fun drawGreaterThanMax(canvas: Canvas?) {
        canvas?.drawCircle(
            width.minus(thresholdMargin).minus(mRadius.div(2)),
            height / 2f,
            mRadius,
            mPaint
        )
        mPaint.color = defaultValueTextColor
    }

    private fun drawMaxAndMinTex(canvas: Canvas?, drawLocation: FloatArray) {
        val y = height / 2f + 7f
        canvas?.drawText("$mMinValue", drawLocation[0], y, mPaint)
        canvas?.drawText(
            "$mMaxValue",
            drawLocation[3],
            y,
            mPaint
        )
    }

    fun setValueChangeListener(onSetValue: (Int) -> Unit = {}) {
        this.mOnSetValue = onSetValue
    }

    // 后台下发
    fun setValue(offset: Int, current: Int) {
        if (current == mCurrentValue) return
        mMinValue = if (current <= offset) {
            0
        } else {
            current.minus(5)
        }
        mMaxValue = current.plus(offset)
        if (mIsOpenAuto) {
            mCurrentValue = current
            this.mOnSetValue(mCurrentValue)
        }
        postInvalidate()
    }


    /**
     * 后台返回，主要是派对界面
     */
    fun setValueSection(min: Int, max: Int) {
        if ((min == mMinValue && max == mMaxValue)||min==max) return
        val oldDenominator = mMaxValue.minus(mMinValue).toFloat()
        val oldMolecule = mCurrentValue.minus(mMinValue)
        val oldMinValue = mMinValue
        val oldMaxValue = mMaxValue
        mMinValue = min
        mMaxValue = max
        if (mIsOpenAuto) {
            when {
                mCurrentValue in oldMinValue..oldMaxValue -> {
                    val denominator = mMaxValue.minus(mMinValue).toFloat()
                    val proportion = oldMolecule.div(oldDenominator)
                    val newLy = denominator.times(proportion).toInt()
                    mCurrentValue = newLy.plus(mMinValue)
                }
                mCurrentValue < oldMinValue -> {
                    mCurrentValue = mMinValue
                }
                mCurrentValue > oldMaxValue -> {
                    mCurrentValue = mMaxValue
                }
            }
            this.mOnSetValue(mCurrentValue)
        }
        postInvalidate()
    }


    fun setValueSection2(min: Int, max: Int) {
        if (min == mMinValue && max == mMaxValue) return
        mMinValue = min
        mMaxValue = max
        postInvalidate()
    }

    //用户手动设置
    fun setCurrentValue(current: Int) {
        if (current == this.mCurrentValue) return
        this.mCurrentValue = current
//        Log.i("IntervalView","9527 setCurrentValue mProportion=" + mProportion)
        postInvalidate()
    }

    //是否启用autofollow
    fun isAutoFollow(isOpenAuto: Boolean) {
        this.mIsOpenAuto = isOpenAuto
        if (mIsOpenAuto) {
            this.mOnSetValue(mCurrentValue)
        }
        postInvalidate()
    }

    /**
     * 设置autofollow为之前定义的值
     */
    fun openAutoFollow(isOpenAuto: Boolean) {
        this.mIsOpenAuto = isOpenAuto
        if (isOpenAuto) {
            if (mCurrentValue !in mMinValue..mMaxValue) {
                mCurrentValue = mMinValue.plus(mMaxValue).div(2)
                this.mOnSetValue(mCurrentValue)
            }
        }
        postInvalidate()
    }
}