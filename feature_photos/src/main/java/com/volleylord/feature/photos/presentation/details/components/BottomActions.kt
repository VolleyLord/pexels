package com.volleylord.feature.photos.presentation.details.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed

@Composable
fun BottomActions(
  isBookmarked: Boolean,
  onDownloadClick: () -> Unit,
  onBookmarkClick: () -> Unit,
  modifier: Modifier = Modifier,
  actionsHeightDp: Int = 48,
  downloadWidthDp: Int = 180
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(actionsHeightDp.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val downloadArrowResId = painterResourceId("download_arrow")
    DownloadButton(
      width = downloadWidthDp.dp,
      height = actionsHeightDp.dp,
      iconSize = 48.dp,
      iconArrowSize = 12.dp,
      iconResId = downloadArrowResId,
      onClick = onDownloadClick
    )

    Spacer(modifier = Modifier.weight(1f))

    val bookmarkResId = bookmarkPainterResId(isBookmarked)
    Box(
      modifier = Modifier
        .size(48.dp)
        .background(color = LightGray, shape = RoundedCornerShape(24.dp))
        .clickable(onClick = onBookmarkClick),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = bookmarkResId),
        contentDescription = "Bookmark"
      )
    }
  }
}

@Composable
private fun DownloadButton(
  width: androidx.compose.ui.unit.Dp,
  height: androidx.compose.ui.unit.Dp,
  iconSize: androidx.compose.ui.unit.Dp,
  iconArrowSize: androidx.compose.ui.unit.Dp,
  iconResId: Int,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .width(width)
      .height(height)
      .background(color = LightGray, shape = RoundedCornerShape(24.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier
        .height(height)
        .width(width)
        .background(Color.Transparent),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(iconSize)
          .background(color = PrimaryRed, shape = RoundedCornerShape(iconSize / 2)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = iconResId),
          contentDescription = null,
          modifier = Modifier.size(iconArrowSize)
        )
      }

        Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = "Download",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black
      )
    }
  }
}

@Composable
private fun painterResourceId(name: String): Int {
  val context = androidx.compose.ui.platform.LocalContext.current
  val id = context.resources.getIdentifier(name, "drawable", context.packageName)
  return if (id != 0) id else android.R.drawable.stat_sys_download
}

@Composable
private fun bookmarkPainterResId(isBookmarked: Boolean): Int {
  val context = androidx.compose.ui.platform.LocalContext.current
  val active = context.resources.getIdentifier("bookmark_active", "drawable", context.packageName)
  val inactive = context.resources.getIdentifier("bookmark_inactive", "drawable", context.packageName)
  return when {
    isBookmarked && active != 0 -> active
    !isBookmarked && inactive != 0 -> inactive
    isBookmarked -> android.R.drawable.star_big_on
    else -> android.R.drawable.star_big_off
  }
}


