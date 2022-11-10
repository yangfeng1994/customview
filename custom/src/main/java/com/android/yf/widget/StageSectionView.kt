package com.android.yf.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.View
import java.math.BigDecimal
import kotlin.math.roundToInt


class StageSectionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    /**
     * 统一的公共的
     */
    val outerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    var distanceRectF = RectF(1F, 1F, 1F, 1F)
    var loacalDistanceRectF = RectF(1F, 1F, 1F, 1F)
    val itemSectionData = arrayListOf(0, 40, 80, 120, 160, 200, 240)
    val progressColor = intArrayOf(
        Color.parseColor("#FF00FFFF"),
        Color.parseColor("#00FF91"),
        Color.parseColor("#FF9AC340"),
        Color.parseColor("#FFE4E700"),
        Color.parseColor("#FFE7BD00"),
        Color.parseColor("#FFEB7100"),
        Color.parseColor("#FFAC3004")
    )

    val mTypeface by lazy { Typeface.createFromAsset(context.assets, "Roboto-Regular.ttf") }

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

    /**
     * 每个小进度之接的距离
     */
    val space = 2

    /**
     * 每个小进度的宽度
     */
    val itemWidth = 35

    /**
     * 开始的左边进度背景
     */
    var externalRound = floatArrayOf(100f, 100f, 0f, 0f, 0f, 0f, 100f, 100f)
    var starInnerRadii = floatArrayOf(100f, 0f, 0f, 0f, 0f, 0f, 0f, 100f)
    val startRoundRect = RoundRectShape(externalRound, null, externalRound)
    val startDrawable = ShapeDrawable(startRoundRect)
    val gradientDrawable = GradientDrawable().apply {
        setStroke(1, progressColor[0])
        shape = GradientDrawable.RECTANGLE
        setColor(Color.TRANSPARENT)
        cornerRadii = externalRound
    }
    var rect: Rect = Rect()

    /**
     * 进度最左边的开始点
     */
    var progressLeft = 60
    val starPath = intArrayOf(0, progressLeft, 10, progressLeft.plus(itemWidth))
    val rectangleRect = RoundRectShape(
        outerRadii,
        null,
        outerRadii
    )
    val towDrawable = ShapeDrawable(rectangleRect)
    val progressBackground = Color.parseColor("#33FFFFFF")
    val color60 = Color.parseColor("#99FFFFFF")

    /**
     * 结束的进度背景
     */
    var endInsideRound = floatArrayOf(0f, 0f, 100f, 100f, 100f, 100f, 0f, 0f)
    val endRoundRect = RoundRectShape(endInsideRound, null, endInsideRound)
    val endDrawable = ShapeDrawable(endRoundRect)

    val startRoundRectProgress = RoundRectShape(externalRound, distanceRectF, starInnerRadii)
    val startDrawableProgress = ShapeDrawable(startRoundRectProgress)

    val startSolidRoundRectProgress = RoundRectShape(externalRound, null, starInnerRadii)
    val startSolidDrawableProgress = ShapeDrawable(startSolidRoundRectProgress)


    val localRoundRectProgress = RoundRectShape(outerRadii, loacalDistanceRectF, outerRadii)
    val localDrawableProgress = ShapeDrawable(localRoundRectProgress)

    val localSolidRoundRectProgress = RoundRectShape(outerRadii, null, outerRadii)
    val localSolidDrawableProgress = ShapeDrawable(localSolidRoundRectProgress)

    var mCustomFtp: Int = 120


    var ftpPercent: Int = 0

    val mPaint by lazy { Paint().apply { typeface = mTypeface } }

    /**
     * 开始的路径
     */
    private fun drawStarSectionProgressBackground(canvas: Canvas) {
        rect.top = starPath[0]
        rect.left = starPath[1]
        rect.bottom = starPath[2]
        rect.right = starPath[3]
        startDrawable.bounds = rect
        if (stage > 1) {
            startDrawable?.paint?.color = progressColor[0]
        } else {
            startDrawable?.paint?.color = progressBackground
        }
        startDrawable?.draw(canvas)
    }

    /**
     * 第二个到第六区块区域的底部进度
     */
    private fun drawRectangleBackground(canvas: Canvas) {
        for (i in 1..5) {
            val left = starPath[1].plus(itemWidth.times(i)).plus(space.times(i))
            rect.top = starPath[0]
            rect.left = left
            rect.bottom = starPath[2]
            rect.right = left.plus(itemWidth)
            val paint = towDrawable.paint
            if (stage > i + 1) {
                paint.color = progressColor[i]
            } else {
                paint.color = progressBackground
            }
            towDrawable.bounds = rect
            towDrawable?.draw(canvas)
        }

    }

    /**
     * 绘制最后的模块的背景
     */
    private fun drawEndSectionProgressBackground(canvas: Canvas) {
        val left = starPath[1].plus(itemWidth.times(6)).plus(space.times(6))
        rect.top = starPath[0]
        rect.left = left
        rect.bottom = starPath[2]
        rect.right = left.plus(itemWidth)
        endDrawable.bounds = rect
        val paint = endDrawable.paint
        if (stage > 6) {
            paint.color = progressColor[6]
        } else {
            paint.color = progressBackground
        }
        endDrawable.draw(canvas)
    }

    private fun drawProgressBackground(canvas: Canvas) {
        drawStarSectionProgressBackground(canvas)
        drawRectangleBackground(canvas)
        drawEndSectionProgressBackground(canvas)
    }

    /**
     * 设置统一的背景
     */
    private fun onInitCommonPath() {
        mPaint.isAntiAlias = true
        startDrawable?.paint?.apply {
            isAntiAlias = true
            color = progressBackground
        }
        towDrawable?.paint?.apply {
            isAntiAlias = true
            color = progressBackground
        }
        endDrawable?.paint?.apply {
            isAntiAlias = true
            color = progressBackground
        }
        startDrawableProgress?.paint?.apply {
            isAntiAlias = true
            color = progressColor[0]
        }
        localDrawableProgress?.paint?.isAntiAlias = true
        localSolidDrawableProgress?.paint?.isAntiAlias = true
        startSolidDrawableProgress?.paint?.apply {
            isAntiAlias = true
            color = progressColor[0]
        }
    }

    /**
     * 绘制进度
     */
    private fun drawProgress(canvas: Canvas) {
        when (stage) {
            0 -> {
                rect.top = starPath[0]
                rect.left = starPath[1]
                rect.bottom = starPath[2]
                rect.right = starPath[3]
                gradientDrawable.bounds = rect
                gradientDrawable?.draw(canvas)
            }
            1 -> {
                rect.top = starPath[0]
                rect.left = starPath[1]
                rect.bottom = starPath[2]
                rect.right = starPath[3]
                startDrawableProgress.bounds = rect
                startDrawableProgress.draw(canvas)
                rect.right = starPath[1].plus(stageProgress)
                startSolidDrawableProgress.bounds = rect
                startSolidDrawableProgress.draw(canvas)
            }
            2, 3, 4, 5, 6 -> {
                val current = stage.minus(1)
                val left = starPath[1].plus(itemWidth.times(current).plus(space.times(current)))
                rect.top = starPath[0]
                rect.left = left
                rect.bottom = starPath[2]
                rect.right = left.plus(itemWidth)
                localDrawableProgress?.paint.color = progressColor[stage.minus(1)]
                localDrawableProgress.bounds = rect
                localDrawableProgress.draw(canvas)
                localSolidDrawableProgress?.paint.color = progressColor[stage.minus(1)]
                rect.right = left.plus(stageProgress)
                localSolidDrawableProgress.bounds = rect
                localSolidDrawableProgress.draw(canvas)
            }
        }
    }

    val ftpRect = Rect()

    /**
     * 绘制标题与ftp
     */
    private fun drawTitle(canvas: Canvas) {
        mPaint.color = color60
        mPaint.textSize = 12F
        val leftTitle = "ZONE"
        val fontMetrics = mPaint.fontMetrics
        val textHeight = fontMetrics.descent.minus(fontMetrics.ascent).minus(3)
        canvas?.drawText(leftTitle, 0F, textHeight, mPaint)
        val ftpTitle = "FTP"
        mPaint.getTextBounds(ftpTitle, 0, ftpTitle.length, ftpRect)
        val ftpWidth = ftpRect.width().toFloat()
        canvas?.drawText(ftpTitle, width.minus(ftpWidth), textHeight, mPaint)
        drawValues(textHeight.plus(30), canvas)
    }

    /**
     * 绘制zone值与ftp值
     */
    private fun drawValues(top: Float, canvas: Canvas) {
        mPaint.color = Color.WHITE
        mPaint.textSize = 20F
        canvas?.drawText("$stage", 0F, top, mPaint)
        val ftpText = "${ftpPercent}%"
        mPaint.getTextBounds(ftpText, 0, ftpText.length, ftpRect)
        val ftpWidth = ftpRect.width().toFloat()
        canvas?.drawText(ftpText, width.minus(ftpWidth).minus(2), top, mPaint)
        drawRange(canvas)
    }

    /**
     * 设置进度区与刻度
     */
    private fun drawRange(canvas: Canvas) {
        mPaint.textSize = 14F
        for (i in 0..6) {
            val left = starPath[1].plus(i.times(itemWidth)).plus(i.times(space)).toFloat()
            val right = left.plus(1)
            val top = 14F
            val bottom = top.plus(4)
            drawLines(left, top, right, bottom, canvas, i)
            canvas.drawText("${itemSectionData[i]}", left, bottom.plus(20), mPaint)
        }
    }

    val lineArray = arrayListOf<LinearGradient>()

    /**
     * 绘制渐变颜色线条
     */
    private fun drawLines(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        canvas: Canvas,
        position: Int,
    ) {
        val line =
            if (lineArray.size == 7) {
                lineArray[position]
            } else {
                val linearGradient = LinearGradient(
                    left,
                    top,
                    right,
                    bottom,
                    intArrayOf(Color.WHITE, Color.parseColor("#00FFFFFF")),
                    null,
                    Shader.TileMode.MIRROR
                )
                lineArray.add(linearGradient)
                linearGradient
            }
        mPaint.shader = line
        canvas.drawRect(
            left,
            top,
            right,
            bottom,
            mPaint
        )
        mPaint.shader = null
    }


    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        onInitCommonPath()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val newWidth = progressLeft.times(2).plus(itemWidth.times(7)).plus(space.times(6))
        val newHeight = 45
        setMeasuredDimension(newWidth, newHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawProgressBackground(canvas)
        drawProgress(canvas)
        drawTitle(canvas)
    }


    /**
     * 设置ftm的值
     */
    fun setCustomFtp(ftp: Int) {
        mCustomFtp = ftp
        itemSectionData.clear()
        itemSectionData.add(0)
        itemSectionData.add((ftp * 0.55).roundToInt())
        itemSectionData.add((ftp * 0.75).roundToInt())
        itemSectionData.add((ftp * 0.9).roundToInt())
        itemSectionData.add((ftp * 1.05).roundToInt())
        itemSectionData.add((ftp * 1.2).roundToInt())
        itemSectionData.add((ftp * 1.5).roundToInt())
        postInvalidate()
    }

    /**
     * 设置输出功率值
     */
    fun setPowerOutPut(outPut: Int) {
        this.outPut = outPut
        setProgress(outPut)
        setFtp(outPut)
        postInvalidate()
    }

    /**
     * 计算ftp值
     */
    private fun setFtp(outPut: Int) {
        ftpPercent = if (mCustomFtp == 0) {
            0
        } else {
            div(outPut.toDouble() * 100, mCustomFtp.toDouble(), 0).toInt()
        }
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    fun div(v1: Double, v2: Double, scale: Int): Double {
        require(scale >= 0) { "The scale must be a positive integer or zero" }
        val b1 = BigDecimal(v1.toString())
        val b2 = BigDecimal(v2.toString())
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    private fun setProgress(outPut: Int) {
        stageProgress = when (outPut) {
            in itemSectionData[0] until 1 -> {
                stage = 0
                0
            }
            in itemSectionData[0] + 1 until itemSectionData[1] -> {  //0-80
                stage = 1
                outPut.times(itemWidth).toFloat().div(itemSectionData[1]).toInt()
            }
            in itemSectionData[1] until itemSectionData[2] -> { //80-110
                stage = 2
                val residue = outPut.minus(itemSectionData[1])
                val sum = itemSectionData[2].minus(itemSectionData[1])
                residue.times(itemWidth).toFloat().div(sum).toInt()
            }
            in itemSectionData[2] until itemSectionData[3] -> {//110-130
                stage = 3
                val residue = outPut.minus(itemSectionData[2])
                val sum = itemSectionData[3].minus(itemSectionData[2])
                residue.times(itemWidth).toFloat().div(sum).toInt()
            }
            in itemSectionData[3] until itemSectionData[4] -> {//130-150
                stage = 4
                val residue = outPut.minus(itemSectionData[3])
                val sum = itemSectionData[4].minus(itemSectionData[3])
                residue.times(itemWidth).toFloat().div(sum).toInt()
            }
            in itemSectionData[4] until itemSectionData[5] -> {//150-180
                stage = 5
                val residue = outPut.minus(itemSectionData[4])
                val sum = itemSectionData[5].minus(itemSectionData[4])
                residue.times(itemWidth).toFloat().div(sum).toInt()
            }
            in itemSectionData[5] until itemSectionData[6] -> {
                stage = 6
                val residue = outPut.minus(itemSectionData[5])
                val sum = itemSectionData[6].minus(itemSectionData[5])
                residue.times(itemWidth).toFloat().div(sum).toInt()
            }
            else -> {
                stage = 7
                itemWidth
            }
        }
    }

}