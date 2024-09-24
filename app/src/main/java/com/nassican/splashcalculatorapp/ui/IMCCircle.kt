package com.nassican.splashcalculatorapp.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class IMCCircle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val categories = listOf(
        "Bajo peso" to Color.CYAN,
        "Normal" to Color.GREEN,
        "Sobrepeso" to Color.YELLOW,
        "Obesidad" to Color.RED
    )
    private var bmiValue = 0f

    fun setBMI(bmi: Float) {
        bmiValue = bmi
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (width / 2).coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = width / 2 * 0.9f
        val centerX = width / 2
        val centerY = height

        // Dibujar el arco del medidor
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        categories.forEachIndexed { index, (_, color) ->
            paint.color = color
            val startAngle = 180f + (180f / categories.size) * index
            val sweepAngle = 180f / categories.size
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
        }

        // Calcular el ángulo correcto para la flecha basado en el IMC
        val categoryRanges = listOf(15f, 18.5f, 24.99f, 29.9f, 40f)
        val angle = when {
            bmiValue <= categoryRanges[0] -> 180f
            bmiValue >= categoryRanges.last() -> 360f
            else -> {
                val currentCategoryIndex = categoryRanges.indexOfFirst { bmiValue <= it }
                val previousIMC = if (currentCategoryIndex == 0) categoryRanges[0] else categoryRanges[currentCategoryIndex - 1]
                val startAngle = 180f + (180f / categories.size) * (currentCategoryIndex - 1)
                val range = categoryRanges[currentCategoryIndex] - previousIMC
                startAngle + (180f / categories.size) * ((bmiValue - previousIMC) / range)
            }
        }

        // Dibujar la flecha
        val arrowLength = radius * 0.9f
        val endX = centerX + arrowLength * cos(Math.toRadians(angle.toDouble())).toFloat()
        val endY = centerY + arrowLength * sin(Math.toRadians(angle.toDouble())).toFloat()


        paint.color = Color.BLACK
        paint.strokeWidth = 5f
        canvas.drawLine(centerX, centerY, endX, endY, paint)
    }
}