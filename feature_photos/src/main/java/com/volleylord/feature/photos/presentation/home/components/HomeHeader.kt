package com.volleylord.feature.photos.presentation.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.volleylord.core.domain.models.Collection
import com.volleylord.core.ui.components.AppLinearProgressIndicator
import com.volleylord.core.ui.components.FeaturedCollections
import com.volleylord.core.ui.components.SearchBar

@Composable
fun HomeHeader(
  query: String,
  onQueryChange: (String) -> Unit,
  onSearch: () -> Unit,
  searchIconResId: Int,
  featuredCollections: List<Collection>,
  onCollectionClick: (Collection) -> Unit,
  showTopProgress: Boolean,
  showBelowFeaturedProgress: Boolean,
  isLandscape: Boolean,
  modifier: Modifier = Modifier
) {
  SearchBar(
    query = query,
    onQueryChange = onQueryChange,
    onSearchClick = onSearch,
    searchIconResId = searchIconResId,
    modifier = modifier
      .then(Modifier)
      .padding(
        top = 12.dp,
        start = com.volleylord.core.ui.theme.Spacing.horizontal,
        end = com.volleylord.core.ui.theme.Spacing.horizontal
      )
  )

  if (showTopProgress) {
    AppLinearProgressIndicator(
      modifier = Modifier
        .padding(
          top = com.volleylord.core.ui.theme.Spacing.featuredCollectionsTop,
          start = com.volleylord.core.ui.theme.Spacing.horizontal,
          end = com.volleylord.core.ui.theme.Spacing.horizontal
        )
    )
  }

  FeaturedCollections(
    collections = featuredCollections,
    onCollectionClick = onCollectionClick,
    modifier = Modifier
      .padding(
        top = if (isLandscape) 0.dp else com.volleylord.core.ui.theme.Spacing.imagesGridTop,
        start = com.volleylord.core.ui.theme.Spacing.horizontal,
        end = com.volleylord.core.ui.theme.Spacing.horizontal
      )
  )

  if (showBelowFeaturedProgress) {
    AppLinearProgressIndicator(
      modifier = Modifier
        .padding(
          top = 8.dp,
          start = com.volleylord.core.ui.theme.Spacing.horizontal,
          end = com.volleylord.core.ui.theme.Spacing.horizontal
        )
    )
  }
}


