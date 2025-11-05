package com.volleylord.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = com.volleylord.core.ui.theme.Spacing.featuredCollectionsTop,
                start = com.volleylord.core.ui.theme.Spacing.horizontal,
                end = com.volleylord.core.ui.theme.Spacing.horizontal
            ),
        color = PrimaryRed,
        trackColor = LightGray
    )
}

