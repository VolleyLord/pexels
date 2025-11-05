package com.volleylord.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.volleylord.core.ui.theme.ComponentSizes
import com.volleylord.core.ui.theme.Gray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White

/**
 * Bottom navigation bar with home and bookmarks icons.
 * @param selectedTab (0 = Home, 1 = Bookmarks).
 * @param onTabSelected Callback when a tab is selected
 * @param modifier
 * @param homeIconActiveResId
 * @param homeIconInactiveResId
 * @param bookmarkIconActiveResId
 * @param bookmarkIconInactiveResId
 */
@Composable
fun BottomBar(
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    homeIconActiveResId: Int = android.R.drawable.ic_menu_recent_history,
    homeIconInactiveResId: Int = android.R.drawable.ic_menu_recent_history,
    bookmarkIconActiveResId: Int = android.R.drawable.star_big_on,
    bookmarkIconInactiveResId: Int = android.R.drawable.star_big_off
) {
    val bottomBarHeight = 64.dp
    val iconSize = 24.dp
    val selectorHeight = 2.dp

    // use Card for box-shadow effect
    Card(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(0.dp),
                ambientColor = Color(0x1F1E1E1E),
                spotColor = Color(0x1E1E1E0F)
            )
            .fillMaxWidth()
            .height(bottomBarHeight),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Common horizontal padding to align selector exactly above icons
            val horizontalPadding = com.volleylord.core.ui.theme.Spacing.horizontal

            // Selector row aligned over icons (24x2, radius 10)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .height(selectorHeight),
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                    if (selectedTab == 0) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(selectorHeight)
                                .background(PrimaryRed, RoundedCornerShape(10.dp))
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                    if (selectedTab == 1) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(selectorHeight)
                                .background(PrimaryRed, RoundedCornerShape(10.dp))
                        )
                    }
                }
            }

            // Icons row (same padding to align with selector)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight - selectorHeight)
                    .padding(horizontal = horizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    BottomBarIcon(
                        iconActiveResId = homeIconActiveResId,
                        iconInactiveResId = homeIconInactiveResId,
                        isSelected = selectedTab == 0,
                        onClick = { onTabSelected(0) },
                        iconSize = iconSize
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    BottomBarIcon(
                        iconActiveResId = bookmarkIconActiveResId,
                        iconInactiveResId = bookmarkIconInactiveResId,
                        isSelected = selectedTab == 1,
                        onClick = { onTabSelected(1) },
                        iconSize = iconSize
                    )
                }
            }
        }
    }
}

/**
 * Individual bottom bar icon using drawable resources.
 *
 * @param iconActiveResId
 * @param iconInactiveResId
 * @param isSelected
 * @param onClick Callback when the icon is clicked.
 * @param modifier
 * @param iconSize
 */
@Composable
private fun BottomBarIcon(
    iconActiveResId: Int,
    iconInactiveResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = modifier
            .size(iconSize)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = if (isSelected) iconActiveResId else iconInactiveResId
            ),
            contentDescription = if (isSelected) "Selected" else "Unselected",
            modifier = Modifier.size(iconSize)
        )
    }
}
