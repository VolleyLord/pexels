package com.volleylord.feature_bookmarks.bookmarks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.volleylord.common.R
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.PexelsTheme

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
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(R.string.bookmarks_empty_title),
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = Color(0xFF333333),
        textAlign = TextAlign.Center,
        modifier = Modifier
          .padding(bottom = 12.dp)
      )

      // Explore button
      Text(
        text = stringResource(R.string.bookmarks_empty_explore),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = PrimaryRed,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .clickable(onClick = onExploreClick)
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun EmptyBookmarksStubPreview() {
  PexelsTheme {
    EmptyBookmarksStub(onExploreClick = { })
  }
}
