package com.volleylord.core.ui.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Composable function that renders an image with a placeholder while loading.
 *
 * @param imageUrl The URL of the image to load.
 * @param placeholder The painter to display as a placeholder while the image is loading.
 * @param contentDescription The accessibility description for the image, or null if decorative.
 * @param contentScale The scale to apply to the image content.
 * @param modifier The modifier for the composable.
 */
@Composable
fun AsyncImageWithPlaceholder(
  imageUrl: String?,
  placeholder: Painter,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop
) {
  AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
      .data(imageUrl)
      .crossfade(300) // Smooth transition
      .memoryCachePolicy(CachePolicy.ENABLED)
      .diskCachePolicy(CachePolicy.ENABLED)
      .build(),
    placeholder = placeholder,
    error = ColorPainter(Gray),
    contentDescription = contentDescription,
    contentScale = contentScale,
    modifier = modifier
  )
}