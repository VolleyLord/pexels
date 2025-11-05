package com.volleylord.feature.photos.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.components.AppendErrorItem
import com.volleylord.core.ui.components.shimmerEffect
import com.volleylord.core.ui.theme.Spacing
import com.volleylord.feature.photos.photos.components.PhotoItem

@Composable
fun HomePhotoGrid(
  lazyPagingItems: LazyPagingItems<Photo>,
  onPhotoClick: (Int) -> Unit,
  modifier: Modifier = Modifier,
  topPadding: Dp = Spacing.imagesGridTop
) {
  val gridState = rememberLazyStaggeredGridState()
  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp
  val screenHeight = configuration.screenHeightDp
  val isLandscape = screenWidth > screenHeight
  val columnsCount = when {
    isLandscape && screenWidth >= 600 -> 4
    isLandscape -> 3
    screenWidth >= 600 -> 3
    else -> 2
  }

  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(columnsCount),
    state = gridState,
    modifier = modifier
      .fillMaxSize()
      .padding(
        top = topPadding,
        start = Spacing.horizontal,
        end = Spacing.horizontal
      ),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalItemSpacing = 10.dp
  ) {
    items(
      count = lazyPagingItems.itemCount,
      key = { index -> lazyPagingItems.peek(index)?.id ?: index }
    ) { index ->
      val photo = lazyPagingItems[index]
      if (photo != null) {
        val aspectRatio = calculateAspectRatio(photo.width, photo.height)
        val appeared = remember(key1 = photo.id) { mutableStateOf(false) }
        LaunchedEffect(photo.id) { appeared.value = true }
        val alpha = animateFloatAsState(
          targetValue = if (appeared.value) 1f else 0f,
          animationSpec = tween(durationMillis = 200),
          label = "grid_item_fade_in"
        )

        PhotoItem(
          photo = photo,
          onClick = { onPhotoClick(photo.id) },
          modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer(alpha = alpha.value)
        )
      } else {
        Card(
          modifier = Modifier
            .aspectRatio(155f/170f)
            .shimmerEffect(120.dp),
          elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp),
          shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
      }
    }

    when (val appendState = lazyPagingItems.loadState.append) {
      is LoadState.Loading -> {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(32.dp),
              strokeWidth = 3.dp
            )
          }
        }
      }
      is LoadState.Error -> {
        item {
          AppendErrorItem(
            error = appendState.error,
            onRetry = { lazyPagingItems.retry() }
          )
        }
      }
      else -> {}
    }
  }
}



private fun calculateAspectRatio(width: Int?, height: Int?): Float {
  return if (width != null && height != null && width > 0 && height > 0) {
    width.toFloat() / height.toFloat()
  } else 1f
}


