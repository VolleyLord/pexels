package com.volleylord.core.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.volleylord.core.ui.theme.ComponentSizes
import com.volleylord.core.ui.theme.Gray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White

/**
 * Bottom navigation bar with home and bookmarks icons.
 *
 * @param selectedTab The currently selected tab (0 = Home, 1 = Bookmarks).
 * @param onTabSelected Callback when a tab is selected.
 * @param modifier Modifier to be applied to the bottom bar.
 */
@Composable
fun BottomBar(
  selectedTab: Int = 0,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
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
            .height(ComponentSizes.bottomBarHeight), // 64dp
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
                .height(ComponentSizes.bottomBarHeight + 8.dp),

            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Icon
                BottomBarIcon(
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.padding(start = 89.dp)
                )

                // Bookmarks Icon
                BottomBarIcon(
                    icon = Icons.Default.Bookmark,
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.padding(end = 89.dp)
                )
            }

            if (selectedTab == 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 101.dp) // left: 89px + (offset = 12)
                        .width(ComponentSizes.selectorIconWidth)
                        .height(ComponentSizes.selectorIconHeight)
                        .background(
                            color = PrimaryRed,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            } else if (selectedTab == 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 101.dp)
                        .width(ComponentSizes.selectorIconWidth)
                        .height(ComponentSizes.selectorIconHeight)
                        .background(
                            color = PrimaryRed,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            }
        }
    }
}

/**
 * Individual bottom bar icon with selection indicator.
 *
 * @param icon The icon to display.
 * @param isSelected Whether this icon is currently selected.
 * @param onClick Callback when the icon is clicked.
 * @param modifier Modifier to be applied to the icon.
 */
@Composable
private fun BottomBarIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (isSelected) "Selected" else "Unselected",
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) PrimaryRed else Gray // Clicked icon color
        )
    }
}

