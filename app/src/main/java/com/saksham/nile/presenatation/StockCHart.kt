package com.saksham.nile.presenatation

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.sp

@Composable
fun StockChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Float = 3f,
    gradientColors: List<Color> = listOf(
        lineColor.copy(alpha = 0.5f),
        lineColor.copy(alpha = 0.2f),
        lineColor.copy(alpha = 0f)
    )
) {
    val spacing = 100f
    val graphColor = lineColor
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    }
    val upperValue = remember(data) { data.maxOrNull() ?: 0.0 }
    val lowerValue = remember(data) { data.minOrNull() ?: 0.0 }

    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / data.size
        (data.indices step 2).forEach { i ->
            val info = data[i]
            val hour = i * 24 / data.size

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${hour}h",
                    spacing + i * spacePerHour,
                    size.height,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        val priceStep = (upperValue - lowerValue) / 5
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    String.format("%.2f", lowerValue + priceStep * i),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        var lastX = 0f
        var lastY = 0f
        val strokePath = Path().apply {
            val height = size.height
            data.forEachIndexed { index, price ->
                val nextX = spacing + index * spacePerHour
                val nextY = height - spacing - (price.toFloat() - lowerValue.toFloat()) * (height - spacing) / (upperValue - lowerValue).toFloat()
                if (index == 0) {
                    moveTo(nextX, nextY)
                } else {
                    cubicTo(
                        lastX + (nextX - lastX) / 2f, lastY,
                        lastX + (nextX - lastX) / 2f, nextY,
                        nextX, nextY
                    )
                }
                lastX = nextX
                lastY = nextY
            }
        }

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = lineWidth,
                cap = StrokeCap.Round
            )
        )

        val fillPath = android.graphics.Path(strokePath.asAndroidPath()).apply {
            lineTo(lastX, size.height - spacing)
            lineTo(spacing, size.height - spacing)
            close()
        }

        drawPath(
            path = fillPath.asComposePath(),
            brush = Brush.verticalGradient(gradientColors)
        )
    }
}