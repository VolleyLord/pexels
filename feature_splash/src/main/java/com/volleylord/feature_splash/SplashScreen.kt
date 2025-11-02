package com.volleylord.feature_splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.core.ui.theme.PrimaryRed

/**
 * Splash screen composable that displays app logo and branding.
 *
 * @param modifier Modifier for the composable.
 * @param iconResId
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    iconResId: Int = android.R.drawable.ic_menu_gallery
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    // base elements sizes
    val baseScreenWidth = 375
    val baseScreenHeight = 812

    val baseLogoSplashWidth = 240
    val baseLogoSplashHeight = 240
    val baseIconWidth = 120
    val baseIconHeight = 141
    val baseTextFontSize = 18

    val baseIconTopFromLogo = 49f
    val baseIconLeftFromLogo = 45f
    val baseTextTopFromLogo = 146f
    val baseTextLeftFromLogo = 135f
    
    // scaling based on screen size
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val scale = minOf(
        screenWidth.toFloat() / baseScreenWidth,
        screenHeight.toFloat() / baseScreenHeight
    )
    
    // adaptive sizes
    val logoSplashWidth = (baseLogoSplashWidth * scale).dp
    val logoSplashHeight = (baseLogoSplashHeight * scale).dp
    val iconWidth = (baseIconWidth * scale).dp
    val iconHeight = (baseIconHeight * scale).dp
    val textSize = (baseTextFontSize * scale).sp
    
    // adaptive elements inside logo
    val iconOffsetX = (baseIconLeftFromLogo * scale).dp
    val iconOffsetY = (baseIconTopFromLogo * scale).dp
    val textOffsetX = (baseTextLeftFromLogo * scale).dp
    val textOffsetY = (baseTextTopFromLogo * scale).dp
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryRed),
        contentAlignment = Alignment.Center
    ) {
        // Splash Logo Container
        Box(
            modifier = Modifier
                .width(logoSplashWidth)
                .height(logoSplashHeight),
            contentAlignment = Alignment.TopStart
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .offset(
                        x = iconOffsetX,
                        y = iconOffsetY
                    )
                    .width(iconWidth)
                    .height(iconHeight),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "PexelsApp.",
                modifier = Modifier
                    .offset(
                        x = textOffsetX,
                        y = textOffsetY
                    ),
                fontSize = textSize,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 0.sp
            )
        }
    }
}

