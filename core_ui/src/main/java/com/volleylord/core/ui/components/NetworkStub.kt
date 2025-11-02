package com.volleylord.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.common.R
import com.volleylord.core.ui.theme.PrimaryRed

/**
 * Network Stub component that displays when network request fails and no cached data is available.
 * Displays an icon and a "Try again" button according to Figma specifications.
 *
 * @param onTryAgainClick Callback invoked when "Try again" button is clicked.
 * @param modifier The modifier for the composable.
 */
@Composable
fun NetworkStub(
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val baseScreenWidth = 375 // Base screen width from Figma
    val screenWidth = configuration.screenWidthDp
    val scale = screenWidth.toFloat() / baseScreenWidth

    // Figma specs: container width: 125.53837585449219, height: 147
    val containerWidth = (125.53837585449219 * scale).dp

    // Icon specs: width: 125.53837585449219, height: 100
    val iconWidth = (125.53837585449219 * scale).dp
    val iconHeight = (100 * scale).dp

    // Text specs: width: 84, height: 23, font Mulish, weight 700, size 18px, line-height 100%
    val textWidth = (84 * scale).dp
    val textSize = (18 * scale).sp

    // Get network_stub_icon drawable resource
    val networkIconResId = context.resources.getIdentifier(
        "network_stub_icon",
        "drawable",
        context.packageName
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .width(containerWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Network stub icon
        Image(
            painter = androidx.compose.ui.res.painterResource(
                id = if (networkIconResId != 0) networkIconResId else android.R.drawable.ic_dialog_info
            ),
            contentDescription = "Network error",
            modifier = Modifier
                .width(iconWidth)
                .size(iconHeight),
            contentScale = ContentScale.Fit
        )

        // "Try again" text button
        Text(
            text = stringResource(R.string.error_network_try_again),
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                fontSize = textSize,
                fontWeight = FontWeight.Bold, // 700
                lineHeight = textSize, // 100% line height
                letterSpacing = 0.sp // 0% letter spacing
            ),
            color = PrimaryRed,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(textWidth)
                .clickable(onClick = onTryAgainClick)
        )
    }
}

