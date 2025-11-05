package com.volleylord.feature_bookmarks.bookmarks.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.common.R
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.image.AsyncImageWithPlaceholder

/**
 * Composable function that renders a bookmarked photo item with author information.
 * Based on Figma specs: image with fixed width 155dp and author overlay below.
 *
 * @param photo The photo to display.
 * @param onClick Callback invoked when the photo is clicked.
 * @param modifier
 */

@Composable
fun BookmarkPhotoItem(
  photo: Photo,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val configuration = LocalConfiguration.current
  val baseScreenWidth = 375
  val screenWidth = configuration.screenWidthDp
  val scale = screenWidth.toFloat() / baseScreenWidth

  val imageWidth = (155 * scale).dp
  val authorHeight = (33 * scale).dp
  val authorPadding = 6.dp
  val cornerRadius = 8.dp

  val placeholderColor = photo.avgColor ?: Color.LightGray
  // Aspect ratio is controlled by parent grid for consistent rows

  Box(
    modifier = modifier
      .width(imageWidth)
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
              onClick = onClick
            )
        }
      )
  ) {
    Card(
      modifier = Modifier
        .width(imageWidth)
        .fillMaxWidth(),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      AsyncImageWithPlaceholder(
        imageUrl = photo.tinyThumbnailUrl,
        backgroundColor = placeholderColor,
        contentDescription = stringResource(
          R.string.content_description_photo_item,
          photo.photographer ?: ""
        ),
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(cornerRadius))
      )
    }

      // Inside Card → Box above image
      Card(
          modifier = Modifier
              .width(imageWidth),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          shape = RoundedCornerShape(cornerRadius)
      ) {
          Box(modifier = Modifier.fillMaxSize()) {
              AsyncImageWithPlaceholder(
                  imageUrl = photo.tinyThumbnailUrl,
                  backgroundColor = placeholderColor,
                  contentDescription = stringResource(
                      R.string.content_description_photo_item,
                      photo.photographer ?: ""
                  ),
                  modifier = Modifier
                      .fillMaxSize()
                      .clip(RoundedCornerShape(cornerRadius))
              )

              //  Author overlay
              Box(
                  modifier = Modifier
                      .fillMaxWidth()
                      .height(authorHeight)
                      .align(Alignment.BottomCenter),
                  contentAlignment = Alignment.CenterStart
              ) {
                  Text(
                      text = photo.photographer ?: "",
                      fontSize = (14 * scale).sp,
                      fontWeight = FontWeight.Normal,
                      color = Color.White,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier
                          .fillMaxWidth()
                          .background(
                              color = Color(0x000000).copy(alpha = 0.4f),
                              shape = RoundedCornerShape(
                                  topStart = 0.dp, topEnd = 0.dp,
                                  bottomStart = cornerRadius,
                                  bottomEnd = cornerRadius
                              )
                          )
                          .padding(horizontal = 8.dp, vertical = 6.dp)
                  )
              }
          }
      }
  }
}

