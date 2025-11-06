package com.volleylord.feature.photos.photos.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.volleylord.common.R
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.image.AsyncImageWithPlaceholder

/**
 * Composable function that renders a single photo item in the photo grid.
 *
 * @param photo The photo to display.
 * @param onClick Callback invoked when the photo is clicked.
 * @param modifier
 */
@Composable
fun PhotoItem(
  photo: Photo,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val placeholderColor = photo.avgColor ?: Color.LightGray
  val description = photo.alt ?: stringResource(
    R.string.photo_list_item_attribution,
    photo.photographer ?: ""
  )
  val label = stringResource(R.string.content_description_photo_item, photo.photographer ?: "")
  Card(
    modifier = modifier
      .fillMaxWidth() // use all width of column in staggered grid
      .then(
        run {
          val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
          val pressed by interactionSource.collectIsPressedAsState()
          val scale by animateFloatAsState(
            targetValue = if (pressed) 0.98f else 1f,
            animationSpec = tween(durationMillis = 120),
            label = "press_scale"
          )
          Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
              interactionSource = interactionSource,
                indication = null,
              onClickLabel = label
            ) { onClick() }
        }
      )
      .semantics {
        contentDescription = description
        role = Role.Image
      },
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    AsyncImageWithPlaceholder(
      imageUrl = photo.thumbnailUrl,
      backgroundColor = placeholderColor,
      contentDescription = description,
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(8.dp)),
      contentScale = ContentScale.FillWidth
    )
  }
}