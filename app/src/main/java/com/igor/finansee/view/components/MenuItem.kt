package com.igor.finansee.view.components
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.igor.finansee.data.models.CircularMenuItem
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MenuItem(
    item: CircularMenuItem,
    index: Int,
    totalItems: Int,
    animationProgress: Float
) {
    val radius = 90.dp
    val angle = 180f + (180f / (totalItems + 1)) * (index + 1)
    val angleInRadians = Math.toRadians(angle.toDouble())

    val x = (radius.value * cos(angleInRadians) * animationProgress).dp
    val y = (radius.value * sin(angleInRadians) * animationProgress).dp

    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .alpha(animationProgress)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { if (animationProgress > 0.9) item.onClick() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            drawCircle(color = item.color, radius = size.minDimension / 2)
        }
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}