package com.volleylord.feature.photos.presentation.details.components

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.volleylord.core.ui.components.shimmerEffect


@Composable
fun DetailsPhotoZoom(
  imageUrl: String?,
  modifier: Modifier = Modifier,
  fixedHeightDp: Int = 586,
  cornerRadiusDp: Int = 24,
  intrinsicWidth: Int? = null,
  intrinsicHeight: Int? = null
) {
  if (imageUrl == null) return

  var scale by remember { mutableFloatStateOf(1f) }
  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }
  var isGestureActive by remember { mutableStateOf(false) }
  var frameWidthPx by remember { mutableFloatStateOf(0f) }
  var frameHeightPx by remember { mutableFloatStateOf(0f) }

  val animatedScale by animateFloatAsState(
    targetValue = if (!isGestureActive) 1f else scale,
    animationSpec = tween(200),
    label = "zoom_reset"
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(cornerRadiusDp.dp))
      .fillMaxWidth()
      .height(fixedHeightDp.dp)
      .onGloballyPositioned { coords ->
        frameWidthPx = coords.size.width.toFloat()
        frameHeightPx = coords.size.height.toFloat()
      }
      .pointerInput(Unit) {
        awaitEachGesture {
          // first touch
          awaitFirstDown(requireUnconsumed = false)
          var active = true
            do {
            val event = awaitPointerEvent()
            val pressed = event.changes.count { it.pressed }
            if (pressed >= 2) {
                val zoomChange = event.calculateZoom()
              val panChange = event.calculatePan()

              val newScale = (scale * zoomChange).coerceIn(1f, 3f)

              val imageAspect = if (intrinsicWidth != null && intrinsicHeight != null && intrinsicHeight > 0) {
                intrinsicWidth.toFloat() / intrinsicHeight.toFloat()
              } else null

              val (baseW, baseH) = if (imageAspect != null && frameWidthPx > 0f && frameHeightPx > 0f) {
                val frameAspect = frameWidthPx / frameHeightPx
                if (frameAspect > imageAspect) {
                  val h = frameHeightPx
                  val w = h * imageAspect
                  w to h
                } else {
                  val w = frameWidthPx
                  val h = w / imageAspect
                  w to h
                }
              } else {
                frameWidthPx to frameHeightPx
              }

              val scaledW = baseW * newScale
              val scaledH = baseH * newScale
              val maxX = kotlin.math.max(0f, (scaledW - frameWidthPx) / 2f)
              val maxY = kotlin.math.max(0f, (scaledH - frameHeightPx) / 2f)

              offsetX = (offsetX + panChange.x).coerceIn(-maxX, maxX)
              offsetY = (offsetY + panChange.y).coerceIn(-maxY, maxY)
              scale = newScale

              event.changes.forEach { it.consume() }
            } else {
              active = false
            }
          } while (active)

            offsetX = 0f
          offsetY = 0f
        }
      }
  ) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
      model = imageUrl,
      contentDescription = null,
      modifier = Modifier
        .fillMaxWidth()
        .height(fixedHeightDp.dp)
        .graphicsLayer(
          scaleX = animatedScale,
          scaleY = animatedScale,
          translationX = offsetX,
          translationY = offsetY
        ),
      contentScale = ContentScale.FillWidth,
      loading = {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(fixedHeightDp.dp)
            .shimmerEffect(120.dp)
        )
      },
      error = {
        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
      }
    )
  }
}