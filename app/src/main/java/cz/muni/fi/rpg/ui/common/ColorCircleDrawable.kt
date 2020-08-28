package cz.muni.fi.rpg.ui.common

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt


class ColorCircleDrawable(@ColorInt color: Int) : Drawable() {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius = 0

    init {
        paint.color = color
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            radius.toFloat(),
            paint
        )
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        radius = Math.min(bounds.width(), bounds.height()) / 2
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

}