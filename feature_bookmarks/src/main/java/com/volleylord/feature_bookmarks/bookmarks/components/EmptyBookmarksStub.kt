package com.volleylord.feature_bookmarks.bookmarks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.volleylord.common.R
import com.volleylord.core.ui.theme.PrimaryRed

/**
 * Empty state stub for when there are no bookmarks.
 * Based on Figma specs with adaptive sizing.
 *
 * @param onExploreClick Callback invoked when the "Explore" button is clicked.
 * @param modifier The modifier for the composable.
 */
@Composable
fun EmptyBookmarksStub(
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val configuration = LocalConfiguration.current
  val baseScreenWidth = 375
  val screenWidth = configuration.screenWidthDp
  val scale = screenWidth.toFloat() / baseScreenWidth

  val containerWidth = (203 * scale).dp
  // val titleTop = (380 * scale).dp.toInt()
    val titleTop = with(LocalDensity.current) { (380 * scale).dp.toPx().toInt() }
    val buttonTop = with(LocalDensity.current) { (410 * scale).dp.toPx().toInt() }
    // val buttonTop = (410 * scale).dp.toInt()

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.width(containerWidth),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Title: "You haven't saved anything yet"
      Text(
        text = stringResource(R.string.bookmarks_empty_title),
        fontSize = (16 * scale).sp,
        fontWeight = FontWeight.Normal,
        color = Color(0xFF333333),
        textAlign = TextAlign.Center,
        modifier = Modifier
          .width(containerWidth)
          .padding(bottom = 8.dp)
      )

      // Explore button
      Text(
        text = stringResource(R.string.bookmarks_empty_explore),
        fontSize = (18 * scale).sp,
        fontWeight = FontWeight.Bold,
        color = PrimaryRed,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .clickable(onClick = onExploreClick)
      )
    }
  }
}
