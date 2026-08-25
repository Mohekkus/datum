package cc.shinemoon.datum.ui.launcher

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    cornerRadius: Dp = 0.dp,
    dashLength: Dp = 8.dp,
    gapLength: Dp = 8.dp
) = composed {
    this.drawBehind {
        val stroke = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
                phase = 0f
            )
        )
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            topLeft = Offset.Zero,
            style = stroke,
        )
    }
}