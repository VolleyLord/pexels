package com.volleylord.core.ui.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed

/**
 * Reusable linear progress indicator with app's theme colors.
 * 
 * @param modifier The modifier for the composable.
 */
@Composable
fun AppLinearProgressIndicator(
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        modifier = modifier,
        color = PrimaryRed,
        trackColor = LightGray
    )
}

