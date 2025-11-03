package com.volleylord.feature_bookmarks.bookmarks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
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
 * @param modifier The modifier for the composable.
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
  val aspectRatio = if (photo.width != null && photo.height != null) {
    photo.width!!.toFloat() / photo.height!!.toFloat()
  } else {
    1f
  }

  Column(
    modifier = modifier
      .width(imageWidth)
      .clickable(onClick = onClick)
  ) {
    Card(
      modifier = Modifier
        .width(imageWidth)
        .aspectRatio(aspectRatio),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      AsyncImageWithPlaceholder(
        imageUrl = photo.tinyThumbnailUrl,
        placeholder = ColorPainter(placeholderColor),
        contentDescription = stringResource(
          R.string.content_description_photo_item,
          photo.photographer ?: ""
        ),
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(cornerRadius))
      )
    }

    // Author overlay
    Box(
      modifier = Modifier
        .width(imageWidth)
        .height(authorHeight)
        .padding(horizontal = authorPadding, vertical = 0.dp),
      contentAlignment = androidx.compose.ui.Alignment.CenterStart
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
            color = Color.Black.copy(alpha = 0.4f),
            shape = RoundedCornerShape(4.dp)
          )
          .padding(horizontal = 8.dp, vertical = 6.dp)
      )
    }
  }
}
