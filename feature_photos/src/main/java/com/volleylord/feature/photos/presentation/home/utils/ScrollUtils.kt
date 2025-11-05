package com.volleylord.feature.photos.presentation.home.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

data class BottomBarScrollState(
    val isVisibleState: androidx.compose.runtime.MutableState<Boolean>,
    val connection: NestedScrollConnection
)

@Composable
fun rememberBottomBarVisibilityOnScroll(): BottomBarScrollState {
    val isVisible = remember { mutableStateOf(true) }
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    isVisible.value = false
                } else if (available.y > 0) {
                    isVisible.value = true
                }
                return Offset.Zero
            }
        }
    }
    return BottomBarScrollState(isVisible, connection)
}


