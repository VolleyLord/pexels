package com.volleylord.core.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Composable function that renders an image with a placeholder while loading.
 *
 * @param imageUrl The URL of the image to load.
 * @param backgroundColor Background color used for placeholder while the image is loading or on error.
 * @param contentDescription The accessibility description for the image, or null if decorative.
 * @param contentScale The scale to apply to the image content.
 * @param modifier
 */
@Composable
fun AsyncImageWithPlaceholder(
  imageUrl: String?,
  backgroundColor: Color,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.FillWidth
) {
  val context = LocalContext.current
  val placeholderIconResId = context.resources.getIdentifier("placeholder_icon", "drawable", context.packageName)

  SubcomposeAsyncImage(
    model = ImageRequest.Builder(context)
      .data(imageUrl)
      .crossfade(300)
      .memoryCachePolicy(CachePolicy.ENABLED)
      .diskCachePolicy(CachePolicy.ENABLED)
      .build(),
    contentDescription = contentDescription,
    contentScale = contentScale,
    modifier = modifier,
    loading = {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(backgroundColor),
        contentAlignment = Alignment.Center
      ) {
        if (placeholderIconResId != 0) {
          Image(
            painter = painterResource(id = placeholderIconResId),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
          )
        }
      }
    },
    error = {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(backgroundColor),
        contentAlignment = Alignment.Center
      ) {
        if (placeholderIconResId != 0) {
          Image(
            painter = painterResource(id = placeholderIconResId),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
          )
        }
      }
    }
  )
}