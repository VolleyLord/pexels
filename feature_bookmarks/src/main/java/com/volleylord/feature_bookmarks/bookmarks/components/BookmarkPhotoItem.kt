package com.volleylord.feature_bookmarks.bookmarks.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.common.R
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.image.AsyncImageWithPlaceholder

private val bookmarkCornerRadius = 20.dp

@Composable
fun BookmarkPhotoItem(
  photo: Photo,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val placeholderColor = photo.avgColor ?: Color.LightGray
  val description = stringResource(
    R.string.content_description_photo_item,
    photo.photographer ?: ""
  )

  val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (pressed) 0.98f else 1f,
    animationSpec = tween(durationMillis = 120),
    label = "bookmark_press_scale"
  )

  Card(
    modifier = modifier
      .graphicsLayer(scaleX = scale, scaleY = scale)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(bookmarkCornerRadius)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      AsyncImageWithPlaceholder(
        imageUrl = photo.thumbnailUrl,
        backgroundColor = placeholderColor,
        contentDescription = description,
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(bookmarkCornerRadius))
      )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(33.dp)
                .background(
                    Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(
                        bottomStart = bookmarkCornerRadius,
                        bottomEnd = bookmarkCornerRadius
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = photo.photographer.orEmpty(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
  }
}
