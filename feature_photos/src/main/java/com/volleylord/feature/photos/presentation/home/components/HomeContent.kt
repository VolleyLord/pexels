package com.volleylord.feature.photos.presentation.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.components.NetworkStub
import com.volleylord.core.ui.components.NoResultsStub
import androidx.compose.ui.unit.dp

@Composable
fun HomeContent(
  pagingStateTag: String,
  isNetworkError: Boolean,
  lazyPagingItems: LazyPagingItems<Photo>,
  onPhotoClick: (Int) -> Unit,
  onLoadPopularPhotos: () -> Unit,
  onSearchQueryChange: (String) -> Unit,
  isLandscape: Boolean,
  modifier: Modifier = Modifier
) {
  when (pagingStateTag) {
    "Error" -> {
      if (isNetworkError && lazyPagingItems.itemCount == 0) {
        NetworkStub(
          onTryAgainClick = { lazyPagingItems.refresh() },
          modifier = modifier
        )
      } else {
        HomePhotoGrid(
          lazyPagingItems = lazyPagingItems,
          onPhotoClick = onPhotoClick,
          modifier = modifier,
          topPadding = if (isLandscape) 0.dp else com.volleylord.core.ui.theme.Spacing.imagesGridTop
        )
      }
    }
    "Empty" -> NoResultsStub(
      onExploreClick = onLoadPopularPhotos,
      onTextClick = { onSearchQueryChange("") },
      modifier = modifier
    )
    "Data" -> HomePhotoGrid(
      lazyPagingItems = lazyPagingItems,
      onPhotoClick = onPhotoClick,
      modifier = modifier,
      topPadding = if (isLandscape) 0.dp else com.volleylord.core.ui.theme.Spacing.imagesGridTop
    )
    else -> {}
  }
}


