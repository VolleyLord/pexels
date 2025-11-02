package com.volleylord.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography styles matching Figma design.
 *  FontFamily.Default used
 * To use Mulish font, add font files to res/font/ and update FontFamily here.
 */
val Typography = Typography(
  bodyLarge = TextStyle(
    fontFamily = FontFamily.Default, // TODO: Replace with Mulish when font files added
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = FontFamily.Default, // Mulish 400 from Figma
    fontWeight = FontWeight.Normal, // 400 = Normal
    fontSize = 14.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.28.sp
  )
)