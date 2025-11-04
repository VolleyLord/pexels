package com.volleylord.core.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue

fun Modifier.shimmerEffect(size: Dp): Modifier = composed {
  val sizePx = with(LocalDensity.current) { size.toPx() }
  val transition = rememberInfiniteTransition(label = "shimmer")
  val startOffsetX by transition.animateFloat(
    initialValue = -2 * sizePx,
    targetValue = 2 * sizePx,
    animationSpec = infiniteRepeatable(tween(1000)),
    label = "shimmer_offset"
  )

  background(
    brush = Brush.linearGradient(
      colors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
      ),
      start = Offset(startOffsetX, 0f),
      end = Offset(startOffsetX + sizePx, sizePx)
    )
  )
}


