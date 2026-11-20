package dev.growbox.ppfd

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

/** Pasek skali z zielonym zakresem docelowym i znacznikiem aktualnego pomiaru. */
class RangeBar(c: Context) : View(c) {
    var min = 100f; var max = 250f; var value = 0f
        set(v) { field = v; invalidate() }
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onMeasure(w: Int, h: Int) = setMeasuredDimension(
        MeasureSpec.getSize(w), (36 * resources.displayMetrics.density).toInt())

    override fun onDraw(c: Canvas) {
        val d = resources.displayMetrics.density
        val top = height / 2f - 5 * d; val bot = height / 2f + 5 * d
        val scaleMax = max * 1.8f
        fun x(v: Float) = (v.coerceIn(0f, scaleMax) / scaleMax) * (width - 20 * d) + 10 * d
        p.color = Color.parseColor("#E6E9E4"); c.drawRoundRect(RectF(2 * d, top, width - 2 * d, bot), 5 * d, 5 * d, p)
        p.color = Color.parseColor("#7CB887"); c.drawRoundRect(RectF(x(min), top, x(max), bot), 5 * d, 5 * d, p)
        val mx = x(value)
        p.color = Color.parseColor("#1F2A22"); c.drawCircle(mx, height / 2f, 9 * d, p)
        p.color = Color.WHITE; c.drawCircle(mx, height / 2f, 4 * d, p)
    }
}
