package com.volleylord.feature_splash.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volleylord.core.ui.theme.PrimaryRed

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    iconResId: Int = android.R.drawable.ic_menu_gallery
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "alpha_animation"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
    }

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

    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val scale = minOf(
        screenWidth.toFloat() / baseScreenWidth,
        screenHeight.toFloat() / baseScreenHeight
    )

    val logoSplashWidth = (baseLogoSplashWidth * scale).dp
    val logoSplashHeight = (baseLogoSplashHeight * scale).dp
    val iconWidth = (baseIconWidth * scale).dp
    val iconHeight = (baseIconHeight * scale).dp
    val textSize = (baseTextFontSize * scale).sp

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
        Box(
            modifier = Modifier
                .width(logoSplashWidth)
                .height(logoSplashHeight)
                .alpha(alpha = alphaAnim.value),
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

            Column(
                modifier = Modifier
                    .offset(
                        x = textOffsetX,
                        y = textOffsetY
                    ),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Pexels",
                    fontSize = textSize,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    letterSpacing = 0.sp
                )
                Text(
                    text = "App.",
                    fontSize = textSize,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    letterSpacing = 0.sp
                )
            }
        }
    }
}


