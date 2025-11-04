package com.volleylord.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoResultsStub(
  onExploreClick: () -> Unit,
  onTextClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val configuration = LocalConfiguration.current
  val baseScreenWidth = 375
  val screenWidth = configuration.screenWidthDp
  val scale = screenWidth.toFloat() / baseScreenWidth

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "No results found",
      style = MaterialTheme.typography.bodyMedium.copy(
        fontSize = (14 * scale).sp,
        fontWeight = FontWeight.Medium,
        lineHeight = (14 * scale).sp,
        letterSpacing = 0.sp
      ),
      textAlign = TextAlign.Center,
      color = com.volleylord.core.ui.theme.TextSecondary,
      modifier = Modifier
        .clickable(onClick = onTextClick)
        .padding(bottom = 8.dp)
    )

    Text(
      text = "Explore",
      style = MaterialTheme.typography.bodyLarge.copy(
        fontSize = (18 * scale).sp,
        fontWeight = FontWeight.Bold,
        lineHeight = (18 * scale).sp,
        letterSpacing = 0.sp
      ),
      textAlign = TextAlign.Center,
      color = com.volleylord.core.ui.theme.PrimaryRed,
      modifier = Modifier.clickable(onClick = onExploreClick)
    )
  }
}


