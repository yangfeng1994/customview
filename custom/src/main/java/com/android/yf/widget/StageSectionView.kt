package com.android.yf.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.roundToInt


class StageSectionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    /**
     * 统一的公共的
     */
    val outerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    var itemSectionData = arrayListOf(0, 40, 80, 120, 160, 200, 240)
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 设置进度的颜色
     */
    var progressColor: ArrayList<Int> = arrayListOf()
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     *0 是没有进行任何阶段
     * 完成的进度的阶段1~7
     */
    var stage = 0

    /**
     * 在某个阶段的进度
     */
    var stageProgress = 0

    /**
     * 当前功率
     */
    private var outPut = 0

    var startDrawable: ShapeDrawable? = null
    var endDrawable: ShapeDrawable? = null

    /**
     * 开始的时候默认显示第一个小方块的进度
     */
    var starStrokeDrawable: GradientDrawable? = null

    var rect: Rect = Rect()

    var startRectProgress: ShapeDrawable? = null

    /**
     * 当前选中进度块的边框
     */
    var localStrokeProgress: ShapeDrawable? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        onInitCommonPath(context, attrs)
    }


    /**
     * 左边的标题的宽度
     */
    var leftTitleWidth = 0

    /**
     * 左边的标题的高度
     */
    var leftTitleHeight = 0

    /**
     * ZONE FTP 字体大小
     */
    var stageTitleTextSize: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * ZONE FTP 字体颜色
     */
    var stageTitleTextColor: Int = 0
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * ZONE FTP value 字体大小
     */
    var stageTitleValueTextSize: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * ZONE FTP value 字体颜色
     */
    var stageTitleValueTextColor: Int = 0
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 每个间隔item 距离 ZONE 与 FTP 左右的边距
     */
    var stageProgressMargin: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 每个间隔item 底部text的字体大小
     */
    var stageProgressItemTextSize: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * ZONE FTP value 字体颜色
     */
    var stageProgressItemTextColor: Int = 0
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 每个间隔item 的高度
     */
    var stageItemHeight: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 每个间隔item 的高度
     */
    var stageItemWidth: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * zone 与ftp 的value 与其距离
     */
    var stageValueMarginTop: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 每一个小区块中间的距离
     */
    var stageProgressSpace: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * 最左跟最右的圆角角度
     */
    var stageProgressCorners: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 进度条背景颜色
     */
    var stageProgressBackground: Int = 0
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 刻度距离progress 的高度
     */
    var stageScaleMargeTop: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 刻度的宽度
     */
    var stageScaleWidth: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * 刻度的高度
     */
    var stageScaleHeight: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 刻度值距离刻度的高度
     */
    var stageItemScaleValueMargeTop: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 刻度值字体的大小
     */
    var stageItemTextSize: Float = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 刻度值字体的颜色
     */
    var stageItemTextColor: Int = 0
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * 进度条边框的宽度
     */
    var stageProgressStrokeWidth: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 左标题
     */
    var stageLeftTitle: String? = null
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 右标题
     */
    var stageRightTitle: String? = null
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * 构建属性
     */
    private fun obtainAttrs(context: Context, attrs: AttributeSet?) {
        val type = context.obtainStyledAttributes(attrs, R.styleable.StageSectionView)
        stageTitleTextSize =
            type.getDimension(
                R.styleable.StageSectionView_stageTitleTextSize,
                resources.getDimension(R.dimen.s_12)
            )
        stageTitleTextColor =
            type.getColor(
                R.styleable.StageSectionView_stageTitleTextColor,
                ContextCompat.getColor(context, R.color.c_60_white)
            )
        stageTitleValueTextSize =
            type.getDimension(
                R.styleable.StageSectionView_stageTitleValueTextSize,
                resources.getDimension(R.dimen.s_16)
            )
        stageTitleValueTextColor =
            type.getColor(
                R.styleable.StageSectionView_stageTitleValueTextColor,
                Color.WHITE
            )
        stageProgressMargin =
            type.getDimension(
                R.styleable.StageSectionView_stageProgressMargin,
                resources.getDimension(R.dimen.d_29)
            )
        stageProgressItemTextSize =
            type.getDimension(
                R.styleable.StageSectionView_stageProgressItemTextSize,
                resources.getDimension(R.dimen.s_12)
            )

        stageProgressItemTextColor =
            type.getColor(
                R.styleable.StageSectionView_stageProgressItemTextColor,
                Color.WHITE
            )

        stageItemHeight =
            type.getDimension(
                R.styleable.StageSectionView_stageItemHeight,
                resources.getDimension(R.dimen.d_10)
            )

        stageItemWidth =
            type.getDimension(
                R.styleable.StageSectionView_stageItemWidth,
                resources.getDimension(R.dimen.d_32)
            )

        stageProgressCorners =
            type.getDimension(
                R.styleable.StageSectionView_stageProgressCorners,
                resources.getDimension(R.dimen.d_95_24)
            )

        stageProgressBackground =
            type.getColor(
                R.styleable.StageSectionView_stageProgressBackground,
                ContextCompat.getColor(context, R.color.c_20_white)
            )

        stageValueMarginTop =
            type.getDimension(
                R.styleable.StageSectionView_stageValueMarginTop,
                resources.getDimension(R.dimen.d_21)
            )

        stageProgressSpace =
            type.getDimension(
                R.styleable.StageSectionView_stageProgressSpace,
                resources.getDimension(R.dimen.d_2)
            )

        stageScaleMargeTop =
            type.getDimension(
                R.styleable.StageSectionView_stageScaleMargeTop,
                resources.getDimension(R.dimen.d_4)
            )

        stageScaleWidth =
            type.getDimension(
                R.styleable.StageSectionView_stageScaleWidth,
                resources.getDimension(R.dimen.d_1)
            )

        stageScaleHeight =
            type.getDimension(
                R.styleable.StageSectionView_stageScaleHeight,
                resources.getDimension(R.dimen.d_4)
            )

        stageItemScaleValueMargeTop =
            type.getDimension(
                R.styleable.StageSectionView_stageItemScaleValueMargeTop,
                resources.getDimension(R.dimen.d_6)
            )

        stageItemTextSize =
            type.getDimension(
                R.styleable.StageSectionView_stageItemTextSize,
                resources.getDimension(R.dimen.s_12)
            )

        stageProgressStrokeWidth =
            type.getDimension(
                R.styleable.StageSectionView_stageProgressStrokeWidth,
                resources.getDimension(R.dimen.d_0_95)
            )

        stageItemTextColor =
            type.getColor(
                R.styleable.StageSectionView_stageItemTextColor,
                Color.WHITE
            )
        stageLeftTitle = type.getString(R.styleable.StageSectionView_stageLeftTitle)
        stageRightTitle = type.getString(R.styleable.StageSectionView_stageRightTitle)
    }

    var starProgressExternalRound: FloatArray? = null

    fun initProgressColor() {
        progressColor = arrayListOf(
            ContextCompat.getColor(context, R.color.c_stage_section_1),
            ContextCompat.getColor(context, R.color.c_stage_section_2),
            ContextCompat.getColor(context, R.color.c_stage_section_3),
            ContextCompat.getColor(context, R.color.c_stage_section_4),
            ContextCompat.getColor(context, R.color.c_stage_section_5),
            ContextCompat.getColor(context, R.color.c_stage_section_6),
            ContextCompat.getColor(context, R.color.c_stage_section_7)
        )
    }

    fun getStarExternalRound(): FloatArray {
        return floatArrayOf(
            stageProgressCorners,
            stageProgressCorners,
            0f,
            0f,
            0f,
            0f,
            stageProgressCorners,
            stageProgressCorners
        )
    }

    private fun getStartShape(): ShapeDrawable {
        return ShapeDrawable(
            RoundRectShape(
                starProgressExternalRound,
                null,
                starProgressExternalRound
            )
        )
    }

    /**
     * 获取默认的开始的边框
     */
    private fun getStarStroke(): GradientDrawable {
        return GradientDrawable().apply {
            setStroke(stageProgressStrokeWidth.roundToInt(), progressColor.first())
            shape = GradientDrawable.RECTANGLE
            setColor(Color.TRANSPARENT)
            cornerRadii = starProgressExternalRound
        }
    }

    /**
     * 第一个色块的进度
     */
    fun starRectProgress(): ShapeDrawable {
        return ShapeDrawable(
            RoundRectShape(
                starProgressExternalRound,
                null,
                starProgressExternalRound
            )
        )
    }

    /**
     * 获取当前进度的正方形边框
     */
    fun getLocalStroke(): ShapeDrawable {
        return ShapeDrawable(
            RoundRectShape(
                outerRadii,
                RectF(
                    stageProgressStrokeWidth,
                    stageProgressStrokeWidth,
                    stageProgressStrokeWidth,
                    stageProgressStrokeWidth
                ),
                outerRadii
            )
        )
    }

    var endInsideRound: FloatArray? = null

    /**
     * 获取最后一个小进度块的list
     */
    fun getInsideRound() {
        endInsideRound = floatArrayOf(
            0f,
            0f,
            stageProgressCorners,
            stageProgressCorners,
            stageProgressCorners,
            stageProgressCorners,
            0f,
            0f
        )
    }

    /**
     * 结束的进度背景
     */
    private fun getEndShape(): ShapeDrawable {
        val endRoundRect = RoundRectShape(endInsideRound, null, endInsideRound)
        return ShapeDrawable(endRoundRect)
    }

    /**
     * 设置统一的背景
     */
    private fun onInitCommonPath(context: Context, attrs: AttributeSet?) {
        obtainAttrs(context, attrs)
        mPaint = Paint()
        mPaint.isAntiAlias = true
        initProgressColor()
        getInsideRound()
        starProgressExternalRound = getStarExternalRound()
        startDrawable = getStartShape()
        endDrawable = getEndShape()
        starStrokeDrawable = getStarStroke()
        startRectProgress = starRectProgress()
        localStrokeProgress = getLocalStroke()
        startDrawable?.paint?.apply {
            isAntiAlias = true
            color = stageProgressBackground
        }
        endDrawable?.paint?.apply {
            isAntiAlias = true
            color = stageProgressBackground
        }
        localStrokeProgress?.paint?.isAntiAlias = true
        startRectProgress?.paint?.apply {
            isAntiAlias = true
            color = progressColor.first()
        }
    }

    /**
     * 需先设置值
     */
    var mCustomFtp: Int = 0

    /**
     * ftp 的百分比值
     */
    var ftpPercent: Int = 0

    lateinit var mPaint: Paint

    /**
     * 设置输出功率值
     * 需优先设置 mCustomFtp ftp值
     */
    fun setPowerOutPut(outPut: Int) {
        this.outPut = outPut
        drawOutPutProgress(outPut)
        calculationFtpPercent(outPut)
        postInvalidate()
    }

    var typeface: Typeface? = null

    /**
     * 设置文字字体
     */
    fun setTypeFace(typeface: Typeface) {
        this.typeface = typeface
    }

    private fun getDisplayMetrics(): DisplayMetrics? {
        return resources.displayMetrics
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val defaultWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 348F, getDisplayMetrics())
                .roundToInt()
        val defaultHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52f, getDisplayMetrics())
                .roundToInt()
        val width = measureHandler(widthMeasureSpec, defaultWidth)
        val height = measureHandler(heightMeasureSpec, defaultHeight)
        setMeasuredDimension(width, height)
    }


    /**
     * 测量
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private fun measureHandler(measureSpec: Int, defaultSize: Int): Int {
        var result = defaultSize
        val measureMode = MeasureSpec.getMode(measureSpec)
        val measureSize = MeasureSpec.getSize(measureSpec)
        if (measureMode == MeasureSpec.EXACTLY) {
            result = measureSize
        } else if (measureMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, measureSize)
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLeftTitle(canvas)
        drawProgressBackground(canvas) {
            drawRange(canvas, it)
        }
        drawProgress(canvas)
    }

    /**
     * 开始的路径
     */
    private fun drawProgressBackground(canvas: Canvas, init: (Float) -> Unit) {
        val top = abs(((leftTitleHeight - stageItemHeight) / 2F))
        val left = getFistStageLeft()
        val bottom = (top + stageItemHeight)
        val right = getFistStageRight()
        rect.top = top.roundToInt()
        rect.left = left
        rect.bottom = bottom.roundToInt()
        rect.right = right
        startDrawable?.bounds = rect
        if (stage > 1) {
            startDrawable?.paint?.color = progressColor.first()
        } else {
            startDrawable?.paint?.color = stageProgressBackground
        }
        startDrawable?.draw(canvas)
        drawRectangleBackground(canvas, top, bottom, left)
        init(bottom)
    }

    var mFistStageLeft = 0

    /**
     * 第一个stage 的宽高
     */
    fun getFistStageLeft(): Int {
        if (mFistStageLeft == 0) {
            mFistStageLeft = leftTitleWidth + stageProgressMargin.roundToInt()
        }
        return mFistStageLeft
    }

    var mFistStageRight = 0

    fun getFistStageRight(): Int {
        if (mFistStageRight == 0) {
            mFistStageRight = getFistStageLeft() + stageItemWidth.roundToInt()
        }
        return mFistStageRight
    }

    /**
     * 第二个到第六区块区域的底部进度
     */
    private fun drawRectangleBackground(canvas: Canvas, top: Float, bottom: Float, left: Int) {
        var endLeft = 0F
        for (i in 1..5) {
            val mLeft =
                left.plus(stageItemWidth.times(i)).plus(stageProgressSpace.times(i))
            val right = mLeft.plus(stageItemWidth)
            if (stage > i + 1) {
                mPaint.color = progressColor[i]
            } else {
                mPaint.color = stageProgressBackground
            }
            canvas.drawRect(mLeft, top, right, bottom, mPaint)
            endLeft = right
        }
        drawEndSectionProgressBackground(canvas, top, bottom, endLeft)
    }

    /**
     * 绘制最后的模块的背景
     */
    private fun drawEndSectionProgressBackground(
        canvas: Canvas,
        top: Float,
        bottom: Float,
        endLeft: Float
    ) {
        val left = endLeft.plus(stageProgressSpace)
        val right = left.plus(stageItemWidth)
        rect.top = top.roundToInt()
        rect.left = left.roundToInt()
        rect.bottom = bottom.roundToInt()
        rect.right = right.roundToInt()
        endDrawable?.bounds = rect
        val paint = endDrawable?.paint
        if (stage > 6) {
            paint?.color = progressColor.last()
        } else {
            paint?.color = stageProgressBackground
        }
        endDrawable?.draw(canvas)
        drawRightTitle(canvas, right)
    }

    /**
     * 绘制右侧的标题
     */
    private fun drawRightTitle(canvas: Canvas, right: Float) {
        val x = right.plus(stageProgressMargin)
        mPaint.color = stageTitleTextColor
        mPaint.textSize = stageTitleTextSize
        mPaint.typeface = typeface
        val rightTitle = stageRightTitle ?: "FTP"
        val fontMetrics = mPaint.fontMetrics
        val dy = (fontMetrics.top - fontMetrics.bottom) / 2 - fontMetrics.top
        val texRect = getTexRect(rightTitle)
        leftTitleWidth = texRect.width()
        val height: Float = fontMetrics.descent - fontMetrics.ascent
        leftTitleHeight = height.roundToInt()
        val baseLine = (height / 2 + dy)
        canvas.drawText(rightTitle, x, baseLine, mPaint)
        val textRight = x + leftTitleWidth
        drawRightValues("${ftpPercent}%", leftTitleHeight.toFloat(), textRight, canvas)
    }

    /**
     * 绘制zone值与ftp值
     */
    private fun drawRightValues(text: String, top: Float, textRight: Float, canvas: Canvas) {
        mPaint.color = stageTitleValueTextColor
        mPaint.textSize = stageTitleValueTextSize
        val fontMetrics = mPaint.fontMetrics
        val dy = (fontMetrics.top - fontMetrics.bottom) / 2 - fontMetrics.top
        val height: Float = fontMetrics.descent - fontMetrics.ascent
        val baseLine = (height / 2 + dy)
        val y = baseLine + top + stageValueMarginTop
        val texRect = getTexRect(text)
        val x = textRight.minus(texRect.width())
        canvas.drawText(text, x, y, mPaint)
    }

    /**
     * 绘制进度
     */
    private fun drawProgress(canvas: Canvas) {
        val top = abs(((leftTitleHeight - stageItemHeight) / 2F))
        val bottom = (top + stageItemHeight)
        when (stage) {
            0 -> {
                val left = getFistStageLeft()
                val right = getFistStageRight()
                rect.top = top.roundToInt()
                rect.left = left
                rect.bottom = bottom.roundToInt()
                rect.right = right
                starStrokeDrawable?.bounds = rect
                starStrokeDrawable?.draw(canvas)
            }

            1 -> {
                val left = getFistStageLeft()
                val right = getFistStageRight()
                rect.top = top.roundToInt()
                rect.left = left
                rect.bottom = bottom.roundToInt()
                rect.right = right
                starStrokeDrawable?.bounds = rect
                starStrokeDrawable?.draw(canvas)
                rect.right = left.plus(stageProgress)
                startRectProgress?.bounds = rect
                startRectProgress?.draw(canvas)
            }

            2, 3, 4, 5, 6 -> {
                val current = stage.minus(1)
                val startLeft = getFistStageLeft()
                val left = startLeft.plus(
                    stageItemWidth.times(current).plus(stageProgressSpace.times(current))
                )
                rect.top = top.roundToInt()
                rect.left = left.roundToInt()
                rect.bottom = bottom.roundToInt()
                rect.right = left.plus(stageItemWidth).roundToInt()
                localStrokeProgress?.paint?.color = progressColor[current]
                localStrokeProgress?.bounds = rect
                localStrokeProgress?.draw(canvas)
                mPaint.color = progressColor[current]
                val right = left.plus(stageProgress)
                canvas.drawRect(left, top, right, bottom, mPaint)
            }
        }
    }


    /**
     * 绘制标题与ftp
     */
    private fun drawLeftTitle(canvas: Canvas) {
        mPaint.color = stageTitleTextColor
        mPaint.textSize = stageTitleTextSize
        mPaint.typeface = typeface
        val leftTitle = stageLeftTitle ?: "ZONE"
        val fontMetrics = mPaint.fontMetrics
        val dy = (fontMetrics.top - fontMetrics.bottom) / 2 - fontMetrics.top
        val texRect = getTexRect(leftTitle)
        leftTitleWidth = texRect.width()
        val height: Float = fontMetrics.descent - fontMetrics.ascent
        leftTitleHeight = height.roundToInt()
        val baseLine = (height / 2 + dy)
        canvas.drawText(leftTitle, 0F, baseLine, mPaint)
        drawLeftValues("$stage", leftTitleHeight.toFloat(), canvas)
    }

    private fun getTexRect(text: String): Rect {
        mPaint.getTextBounds(text, 0, text.length, rect)
        return rect
    }


    /**
     * 绘制zone值与ftp值
     */
    private fun drawLeftValues(text: String, top: Float, canvas: Canvas) {
        mPaint.color = stageTitleValueTextColor
        mPaint.textSize = stageTitleValueTextSize
        val fontMetrics = mPaint.fontMetrics
        val dy = (fontMetrics.top - fontMetrics.bottom) / 2 - fontMetrics.top
        val height: Float = fontMetrics.descent - fontMetrics.ascent
        val baseLine = (height / 2 + dy)
        val y = baseLine + top + stageValueMarginTop
        canvas.drawText(text, 0F, y, mPaint)
    }

    /**
     * 设置进度区与刻度
     */
    private fun drawRange(canvas: Canvas, progressBottom: Float) {
        val start = getFistStageLeft()
        itemSectionData.forEachIndexed { index, i ->
            val left = start.plus(stageItemWidth.roundToInt().times(index))
                .plus(stageProgressSpace.roundToInt().times(index))
            val right = left.plus(stageScaleWidth.roundToInt())
            val top = progressBottom.plus(stageScaleMargeTop).roundToInt()
            val bottom = top.plus(stageScaleHeight).roundToInt()
            drawLines(left, top, right, bottom, canvas, index)
            mPaint.color = stageItemTextColor
            mPaint.textSize = stageItemTextSize
            val fontMetrics = mPaint.fontMetrics
            val dy = (fontMetrics.top - fontMetrics.bottom) / 2 - fontMetrics.top
            val height: Float = fontMetrics.descent - fontMetrics.ascent
            val baseLine = (height / 2 + dy)
            val text = if (index == 0) {
                0
            } else {
                itemSectionData[index] + 1
            }
            canvas.drawText(
                "$text",
                left.toFloat(),
                bottom.plus(stageItemScaleValueMargeTop).plus(baseLine),
                mPaint
            )
        }
    }

    val lineArray = arrayListOf<GradientDrawable>()

    /**
     * 绘制渐变颜色线条
     */
    private fun drawLines(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        canvas: Canvas,
        position: Int,
    ) {
        val line =
            if (lineArray.size == 7) {
                lineArray[position]
            } else {
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(Color.WHITE, Color.parseColor("#00FFFFFF"))
                )
                rect.left = left
                rect.top = top
                rect.right = right
                rect.bottom = bottom
                gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
                gradientDrawable.shape = GradientDrawable.RECTANGLE
                gradientDrawable.bounds = rect
                lineArray.add(gradientDrawable)
                gradientDrawable
            }
        line.draw(canvas)
    }

    /**
     * 计算ftp百分比
     */
    private fun calculationFtpPercent(outPut: Int) {
        ftpPercent = if (mCustomFtp == 0) {
            0
        } else {
            outPut.times(100F).div(mCustomFtp).roundToInt()
        }
    }


    private fun drawOutPutProgress(outPut: Int) {
        stageProgress = when (outPut) {
            in itemSectionData[0] until 5 -> {
                stage = 0
                0
            }

            in itemSectionData[0] + 5..itemSectionData[1] -> {  //0-80
                stage = 1
                outPut.times(stageItemWidth).div(itemSectionData[1]).toInt()
            }

            in itemSectionData[1] + 1..itemSectionData[2] -> { //80-110
                stage = 2
                val residue = outPut.minus(itemSectionData[1])
                val sum = itemSectionData[2].minus(itemSectionData[1])
                residue.times(stageItemWidth).div(sum).toInt()
            }

            in itemSectionData[2] + 1..itemSectionData[3] -> {//110-130
                stage = 3
                val residue = outPut.minus(itemSectionData[2])
                val sum = itemSectionData[3].minus(itemSectionData[2])
                residue.times(stageItemWidth).div(sum).toInt()
            }

            in itemSectionData[3] + 1..itemSectionData[4] -> {//130-150
                stage = 4
                val residue = outPut.minus(itemSectionData[3])
                val sum = itemSectionData[4].minus(itemSectionData[3])
                residue.times(stageItemWidth).div(sum).toInt()
            }

            in itemSectionData[4] + 1..itemSectionData[5] -> {//150-180
                stage = 5
                val residue = outPut.minus(itemSectionData[4])
                val sum = itemSectionData[5].minus(itemSectionData[4])
                residue.times(stageItemWidth).div(sum).toInt()
            }

            in itemSectionData[5] + 1..itemSectionData[6] -> {
                stage = 6
                val residue = outPut.minus(itemSectionData[5])
                val sum = itemSectionData[6].minus(itemSectionData[5])
                residue.times(stageItemWidth).div(sum).toInt()
            }

            else -> {
                stage = 7
                stageItemWidth.roundToInt()
            }
        }
    }

}