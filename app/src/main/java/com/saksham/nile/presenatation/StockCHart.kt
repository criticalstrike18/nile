package com.saksham.nile.presenatation

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.saksham.nile.domain.ChartDataPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Float = 3f,
    gradientColors: List<Color> = listOf(
        lineColor.copy(alpha = 0.5f),
        lineColor.copy(alpha = 0.2f),
        lineColor.copy(alpha = 0f)
    )
) {
    if (data.isEmpty()) {
        Text("No data available")
        return
    }

    val spacing = 100f
    val transparentGraphColor = remember {
        lineColor.copy(alpha = 0.5f)
    }
    val upperValue = remember(data) { data.maxOfOrNull { it.close }?.toFloat() ?: 0f }
    val lowerValue = remember(data) { data.minOfOrNull { it.close }?.toFloat() ?: 0f }
    val dateFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / data.size.coerceAtLeast(1)

        // Draw time labels
        val labelCount = minOf(data.size, 5)
        if (labelCount > 1) {
            val step = (data.size - 1) / (labelCount - 1)
            (0 until labelCount).forEach { i ->
                val index = i * step
                val info = data[index]
                val formattedDate = dateFormatter.format(Date(info.timestamp * 1000))

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        formattedDate,
                        spacing + index * spacePerHour,
                        size.height,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // Draw price labels
        val priceStep = (upperValue - lowerValue) / 5f
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
            data.forEachIndexed { index, point ->
                val nextX = spacing + index * spacePerHour
                val nextY = height - spacing - (point.close.toFloat() - lowerValue) * (height - spacing) / (upperValue - lowerValue).coerceAtLeast(1f)
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
            color = lineColor,
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