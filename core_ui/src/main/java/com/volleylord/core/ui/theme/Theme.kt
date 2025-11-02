package com.volleylord.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Purple80,
  secondary = PurpleGrey80,
  tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
  // Primary color - Red for selected elements and accents
  primary = PrimaryRed,
  onPrimary = White, // White text on red
  
  // Background - White for app background
  background = White,
  onBackground = Dark, // Dark text on white background
  
  // Surface - Light gray for unselected categories
  surface = LightGray,
  onSurface = Dark, // Dark text on light gray surface
  
  // Surface variant - Also light gray for chips/collections
  surfaceVariant = LightGray,
  onSurfaceVariant = Dark, // Dark text on light gray variant
  
  // Outline - Gray for borders and dividers
  outline = Gray,
  outlineVariant = Gray.copy(alpha = 0.5f),
  
  // Secondary - Gray for placeholder and secondary elements
  secondary = Gray,
  onSecondary = White,
  
  // Tertiary - Using pink for accent if needed
  tertiary = Pink40,
  onTertiary = White,
  
  // Error colors (keeping Material defaults)
  error = androidx.compose.ui.graphics.Color(0xFFB00020),
  onError = White
)

@Composable
fun PexelsTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color disabled to use custom Figma colors
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}