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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.volleylord.core.ui.theme.ComponentSizes
import com.volleylord.core.ui.theme.Gray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White

/**
 * Bottom navigation bar with home and bookmarks icons.
 * @param selectedTab The currently selected tab (0 = Home, 1 = Bookmarks).
 * @param onTabSelected Callback when a tab is selected.
 * @param modifier Modifier to be applied to the bottom bar.
 * @param homeIconActiveResId Resource ID (home_button_active).
 * @param homeIconInactiveResId Resource ID  (home_button_inactive).
 * @param bookmarkIconActiveResId Resource ID  (bookmark_active).
 * @param bookmarkIconInactiveResId Resource ID (bookmark_inactive).
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
    val configuration = LocalConfiguration.current

    val baseScreenWidth = 375
    val baseBottomBarHeight = 64
    val baseIconSize = 24
    val baseSelectorWidth = 24
    val baseSelectorHeight = 2

    val baseHomeIconLeft = 89
    val baseBookmarkIconLeft = 262

    val screenWidth = configuration.screenWidthDp
    val scale = screenWidth.toFloat() / baseScreenWidth

    val bottomBarHeight = (baseBottomBarHeight * scale).dp
    val iconSize = (baseIconSize * scale).dp
    val selectorWidth = (baseSelectorWidth * scale).dp
    val selectorHeight = (baseSelectorHeight * scale).dp

    val homeIconLeft = (baseHomeIconLeft * scale).dp
    val bookmarkIconLeft = (baseBookmarkIconLeft * scale).dp

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomBarHeight),
            contentAlignment = Alignment.TopStart
        ) {
            // Selector indicator
            if (selectedTab == 0) {
                Box(
                    modifier = Modifier
                        .offset(x = homeIconLeft, y = 0.dp)
                        .width(selectorWidth)
                        .height(selectorHeight)
                        .background(
                            color = PrimaryRed,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            } else if (selectedTab == 1) {
                Box(
                    modifier = Modifier
                        .offset(x = bookmarkIconLeft, y = 0.dp)
                        .width(selectorWidth)
                        .height(selectorHeight)
                        .background(
                            color = PrimaryRed,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight),
                contentAlignment = Alignment.CenterStart
            ) {
                // Home Icon
                BottomBarIcon(
                    iconActiveResId = homeIconActiveResId,
                    iconInactiveResId = homeIconInactiveResId,
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.offset(x = homeIconLeft),
                    iconSize = iconSize
                )

                // Bookmarks Icon
                BottomBarIcon(
                    iconActiveResId = bookmarkIconActiveResId,
                    iconInactiveResId = bookmarkIconInactiveResId,
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.offset(x = bookmarkIconLeft),
                    iconSize = iconSize
                )
            }
        }
    }
}

/**
 * Individual bottom bar icon using drawable resources.
 *
 * @param iconActiveResId
 * @param iconInactiveResId
 * @param isSelected Whether this icon is currently selected.
 * @param onClick Callback when the icon is clicked.
 * @param modifier Modifier to be applied to the icon.
 * @param iconSize Size of the icon (adaptive from Figma).
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
