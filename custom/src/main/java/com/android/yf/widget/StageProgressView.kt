package com.android.yf.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.android.yf.bean.ProgressStageData

class StageProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textRect = Rect()

    private val data = ArrayList<ProgressStageData>()

    /**
     * 进度条绘制间隔 7.5px
     */
    private var interval = 0F

    /**
     * 进行在第几个阶段
     */
    var currentStage = 0

    /**
     * 已经播放完成的进度
     */
    private var completeProgressColor = Color.WHITE

    /**
     * 正在播放的进度
     */
    private var inProgressColor = Color.parseColor("#64BC1C")

    /**
     * 默认的播放进度底色
     */
    private var defaultProgressColor = Color.parseColor("#33FFFFFF")

    /**
     * progress 距离上面icon的距离
     */
    private var progressMarginTop: Float = 6F

    /**
     * progress 距离底部时间的距离
     */
    private var progressMarginBottom: Float = 6F

    /**
     * 阶段分钟数字体大小
     */
    private var stageTextSize = 15F

    /**
     * 进度条的高度
     */
    private var progressHeight: Float = 6F

    /**
     * 阶段图标的高度
     */
    private var stageMarkHeight: Float = 20F

    /**
     * 图标的宽度
     */
    private var stageMarkWidth: Float = 24F
    private var stagMarkBitmap = HashMap<Int, Bitmap>()
    private val currentStageMarkBitmap = HashMap<Int, Bitmap>()
    private val noStartStageMarkBitmap = HashMap<Int, Bitmap>()
    var rect: Rect = Rect()

    val outerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val progressRoundRect = RoundRectShape(outerRadii, null, outerRadii)
    val progressDrawable = ShapeDrawable(progressRoundRect)

    /**
     * 骑行的阶段
     * 0 准备阶段 1 骑行阶段 2 结束
     */
    open var cyclingStatus: Int = 0
        set(value) {
            field = value
            if (value == 1) {
                val first = data.firstOrNull()
                currentStageListener.invoke(first, 0)
            }
            postInvalidate()
        }

    /**
     * 时长
     */
    open var duration: Long = 0
        set(value) {
            field = value
            postInvalidate()
        }


    /**
     * 视频或者音乐的进度
     */
    var currentPosition: Long = 0
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 是否正在准备阶段
     */
    private fun isPrepare(): Boolean {
        return cyclingStatus == 0
    }

    /**
     * 是否结束
     */
    private fun isOver(): Boolean {
        return cyclingStatus == 2
    }

    /**
     * 当前选中的阶段的监听
     */
    var currentStageListener: (ProgressStageData?, Int) -> Unit = { progressStageData, i -> }

    init {
        paint.strokeWidth = 5f
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        obtain(context, attrs)
    }

    private fun obtain(context: Context, attrs: AttributeSet? = null) {
        val type = context.obtainStyledAttributes(attrs, R.styleable.StageProgressView)
        interval =
            type.getDimension(
                R.styleable.StageProgressView_play_progress_bar_item_interval,
                interval
            )
        progressMarginTop = type.getDimension(
            R.styleable.StageProgressView_play_progress_progress_margin_top,
            progressMarginTop
        )
        progressHeight = type.getDimension(
            R.styleable.StageProgressView_play_progress_progress_height,
            progressHeight
        )
        progressMarginBottom = type.getDimension(
            R.styleable.StageProgressView_play_progress_min_text_margin_top,
            progressMarginBottom
        )
        stageTextSize = type.getDimension(
            R.styleable.StageProgressView_play_progress_min_text_size,
            stageTextSize
        )
        stageMarkHeight =
            type.getDimension(
                R.styleable.StageProgressView_play_progress_icon_height,
                stageMarkHeight
            )
        stageMarkWidth =
            type.getDimension(
                R.styleable.StageProgressView_play_progress_icon_width,
                stageMarkWidth
            )
        completeProgressColor = type.getColor(
            R.styleable.StageProgressView_play_progress_complete_progress_color,
            completeProgressColor
        )
        inProgressColor = type.getColor(
            R.styleable.StageProgressView_play_progress_in_progress_color,
            inProgressColor
        )
        defaultProgressColor = type.getColor(
            R.styleable.StageProgressView_play_progress_default_progress_color,
            defaultProgressColor
        )
        type?.recycle()
        mTextPaint.textSize = stageTextSize
        mTextPaint.color = inProgressColor
    }

    fun setData(data: Collection<ProgressStageData>, duration: Long) {
        this.data.clear()
        this.data.addAll(data)
        this.duration = duration
        postInvalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val mHeight =
            stageMarkHeight.plus(progressMarginTop).plus(progressHeight).plus(progressMarginBottom)
                .plus(stageTextSize).plus(8).toInt()
        when {
            layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                setMeasuredDimension(width, mHeight)
            }
            layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                setMeasuredDimension(width, heightSize)
            }
            layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                setMeasuredDimension(widthSize, mHeight)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isEmpty()) return
        setCurrentStage()
        val unit = calculateDistance()
        drawStage(unit, canvas)
        drawProgress(unit, canvas)
    }

    /**
     * 判断数据是否为空，为空则不绘制
     */
    private fun isEmpty(): Boolean {
        return data.isNullOrEmpty() || duration == 0L
    }

    /**
     * 计算每一毫秒等于的距离
     * unit 1毫秒的px值
     */
    fun calculateDistance(): Float {
        val size = data.size.minus(1)
        val width = width.minus(interval.times(size))
        val unit = width.div(duration)
        return unit
    }

    /**
     * 绘制每个阶段
     */
    private fun drawStage(unit: Float, canvas: Canvas) {
        data.forEachIndexed { index, periodStageData ->
            val left = drawStagMark(unit, index, periodStageData, canvas)
            val progressSize = drawStageProgress(left, unit, index, periodStageData, canvas)
            drawStageText(left, index, periodStageData, progressSize, canvas)
        }
    }

    /**
     * 绘制每个阶段的图标
     */
    private fun drawStagMark(
        unit: Float,
        index: Int,
        periodStageData: ProgressStageData,
        canvas: Canvas,
    ): Float {
        val startTime = periodStageData.start_time
        val left = startTime.times(unit).plus(interval.times(index))
        val type = data[index].type
        val stageMark = when {
            isOver() -> {
                var bitmap = stagMarkBitmap[type]
                if (null == bitmap) {
                    bitmap = getOverCyclingMark(type)
                    stagMarkBitmap[type] = bitmap
                }
                bitmap
            }
            isPrepare() -> {
                var bitmap = noStartStageMarkBitmap[type]
                if (null == bitmap) {
                    bitmap = getNoStartCyclingMark(type)
                    noStartStageMarkBitmap[type] = bitmap
                }
                bitmap
            }
            currentStage == index -> {
                var bitmap = currentStageMarkBitmap[type]
                if (null == bitmap) {
                    bitmap = getOverCyclingMark(type)
                    currentStageMarkBitmap[type] = bitmap
                }
                bitmap
            }
            currentStage > index -> {
                var bitmap = stagMarkBitmap[type]
                if (null == bitmap) {
                    bitmap = getOverCyclingMark(type)
                    stagMarkBitmap[type] = bitmap
                }
                bitmap
            }
            else -> {
                var bitmap = noStartStageMarkBitmap[type]
                if (null == bitmap) {
                    bitmap = getNoStartCyclingMark(type)
                    noStartStageMarkBitmap[type] = bitmap
                }
                bitmap
            }
        }
        if (!stageMark.isRecycled) {
            canvas.drawBitmap(
                stageMark,
                left,
                0F,
                paint
            )
        }
        return left
    }

    /**
     * 绘制每个阶段的进度
     */
    private fun drawStageProgress(
        left: Float,
        unit: Float,
        index: Int,
        periodStageData: ProgressStageData,
        canvas: Canvas,
    ): Float {
        rect.left = left.toInt()
        val time = periodStageData.end_time.minus(periodStageData.start_time)
        val right = time.times(unit).plus(left).toInt()
        val top = stageMarkHeight.plus(progressMarginTop).toInt()
        val bottom = top.plus(progressHeight).toInt()
        rect.right = right
        rect.top = top
        rect.bottom = bottom
        progressDrawable.bounds = rect
        progressDrawable.paint?.color = when (cyclingStatus) {
            0 -> {
                defaultProgressColor
            }
            1 -> {
                if (currentStage > index) {
                    completeProgressColor
                } else {
                    defaultProgressColor
                }
            }
            2 -> {
                completeProgressColor
            }
            else -> {
                completeProgressColor
            }
        }
        progressDrawable.draw(canvas)
        return right.minus(left)
    }

    /**
     * 绘制每个阶段的文字
     */
    private fun drawStageText(
        left: Float,
        index: Int,
        periodStageData: ProgressStageData,
        progressSize: Float,
        canvas: Canvas,
    ) {
        if (isPrepare() || isOver()) return
        if (currentStage == index) {
            val top = stageMarkHeight.plus(progressMarginTop).plus(progressMarginBottom).toInt()
            val bottom =
                top.plus(progressHeight).toInt().plus(progressMarginBottom).plus(stageTextSize)
            val sum = periodStageData.end_time.minus(periodStageData.start_time)
            val surplus = sum % 60
            val min = if (surplus > 30) {
                sum.div(60).plus(1)
            } else {
                sum.div(60)
            }
            val text = "${min}min ${getStageName(data[currentStage].type)}"
            mTextPaint.getTextBounds(text, 0, text.length, textRect)
            val textWidth = textRect.width()
            val size = data.size.minus(1)
            var starLocal = if (progressSize < textWidth && index == size) {
                width.minus(textWidth).toFloat()
            } else {
                left
            }
            mTextPaint.color = completeProgressColor
            canvas.drawText(
                text,
                starLocal,
                bottom,
                mTextPaint
            )
        }
    }

    /**
     * 绘制进度
     */
    private fun drawProgress(unit: Float, canvas: Canvas) {
        if (isPrepare() || isOver()) return
        val periodStageData = data[currentStage]
        val startTime = periodStageData.start_time
        val time = currentPosition.minus(startTime)
        val left = startTime.times(unit).plus(interval.times(currentStage)).toInt()
        val top = stageMarkHeight.plus(progressMarginTop).toInt()
        val bottom = top.plus(progressHeight).toInt()
        val right = time.times(unit).plus(left).toInt()
        rect.left = left
        rect.right = right
        rect.top = top
        rect.bottom = bottom
        progressDrawable.bounds = rect
        progressDrawable.paint?.color = inProgressColor
        progressDrawable.draw(canvas)
    }

    /**
     * 获取当前骑行的阶段
     */
    fun setCurrentStage() {
        data.forEachIndexed { index, periodStageData ->
            if (currentPosition in periodStageData.start_time until periodStageData.end_time) {
                if (currentStage != index) {
                    currentStage = index
                    currentStageListener.invoke(periodStageData, index)
                }
                return
            }
        }
    }

    /**
     * 当前选中的资源文件
     */
    fun getCurrentCyclingMark(type: Int): Bitmap {
        val drawableId = when (type) {
            20 -> {
                R.drawable.stage_warn_up_sel
            }
            30, 40 -> {
                R.drawable.stage_cycling_sel
            }
            60 -> {
                R.drawable.stage_dumbbell_sel
            }
            100 -> {
                R.drawable.stage_emotion_slow_sel
            }
            200 -> {
                R.drawable.stage_over_sel
            }
            else -> {
                R.drawable.stage_warn_up_sel
            }
        }
        return getBitmap(drawableId)
    }

    /**
     * 结束的资源文件
     */
    fun getOverCyclingMark(type: Int): Bitmap {
        val drawableId = when (type) {
            20 -> {
                R.drawable.stage_warn_up
            }
            30, 40 -> {
                R.drawable.stage_cycling
            }
            60 -> {
                R.drawable.stage_dumbbell
            }
            100 -> {
                R.drawable.stage_emotion_slow
            }
            200 -> {
                R.drawable.stage_over
            }
            else -> {
                R.drawable.stage_warn_up
            }
        }
        return getBitmap(drawableId)
    }

    /**
     * 当前选中的资源文件
     */
    fun getNoStartCyclingMark(type: Int): Bitmap {
        val drawableId = when (type) {
            20 -> {
                R.drawable.stage_warn_up_no_start
            }
            30, 40 -> {
                R.drawable.stage_cycling_no_start
            }
            60 -> {
                R.drawable.stage_dumbbell_no_start
            }
            100 -> {
                R.drawable.stage_emotion_slow_no_start
            }
            200 -> {
                R.drawable.stage_over_no_start
            }
            else -> {
                R.drawable.stage_warn_up_no_start
            }
        }
        return getBitmap(drawableId)
    }

    /**
     * 资源文件转换成bitmap
     */
    private fun getBitmap(drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(
            context,
            drawableId
        )
        val rBitmap = Bitmap.createBitmap(
            drawable?.minimumWidth!!,
            drawable?.minimumHeight!!,
            Bitmap.Config.ARGB_8888
        )
        val width = rBitmap.width
        val height = rBitmap.height
        val scaleWidth = stageMarkHeight.div(width)
        val scaleHeight = stageMarkWidth.div(height)
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val bitmap = Bitmap.createBitmap(
            rBitmap,
            0,
            0,
            width,
            height,
            matrix,
            true
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    /**
     * 获取当前状态的名称
     */
    fun getStageName(type: Int): String {
        return when (type) {
            10 -> context.getString(R.string.stage_start_text)
            20 -> context.getString(R.string.stage_warn_up_text)
            30 -> context.getString(R.string.stage_cycling_text)
            40 -> context.getString(R.string.stage_cycling_text)
            50 -> context.getString(R.string.stage_rest_text)
            60 -> context.getString(R.string.stage_upper_body_text)
            70 -> context.getString(R.string.stage_emotion_slow_text)
            80 -> context.getString(R.string.stage_big_sprint_text)
            90 -> context.getString(R.string.stage_get_off_text)
            100 -> context.getString(R.string.stage_stretch_text)
            200 -> context.getString(R.string.stage_over_text)
            else -> ""
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stagMarkBitmap.forEach { it.value.recycle() }
        currentStageMarkBitmap.forEach { it.value.recycle() }
        noStartStageMarkBitmap.forEach { it.value.recycle() }
    }

}