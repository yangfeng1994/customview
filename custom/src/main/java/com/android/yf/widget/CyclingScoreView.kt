package com.android.yf.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.tan


class CyclingScoreView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    val mPaint = Paint().apply {
        isAntiAlias = true
    }

    /**
     * 资源文件转换成bitmap
     * 不是实时postInvalidate 所以此方法中有创建对象的操作
     */
    private fun getBitmap(drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(
            context,
            drawableId
        )
        val rBitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(rBitmap)
        canvas.skew(-0.2F, 0F)
        val radians = Math.toRadians(11.5)
        val tan9 = tan(radians)
        val x = height * tan9
        canvas.drawFilter =
            PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        drawable?.setBounds(abs(x).roundToInt(), 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return rBitmap
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val bitmap = getBitmap(R.drawable.shape_cycling_score_view)
        canvas?.drawBitmap(bitmap, 0F, 0F, mPaint)
        bitmap.recycle()
    }


}