package com.volleylord.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Reusable back button component that uses the app's back_icon drawable.
 * 
 * @param onClick Callback invoked when the button is clicked.
 * @param modifier The modifier for the composable.
 * @param size The size of the button in dp.
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 40
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(size.dp)
    ) {
        val context = LocalContext.current
        val backIconResId = context.resources.getIdentifier(
            "back_icon",
            "drawable",
            context.packageName
        )
        Image(
            painter = painterResource(
                id = if (backIconResId != 0) backIconResId else android.R.drawable.ic_menu_revert
            ),
            contentDescription = "Back"
        )
    }
}

