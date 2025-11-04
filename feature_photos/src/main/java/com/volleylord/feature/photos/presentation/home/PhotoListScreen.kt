package com.volleylord.feature.photos.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import com.volleylord.common.R
import com.volleylord.core.core.network.NetworkErrorUtils
import com.volleylord.core.domain.models.Collection
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.components.FeaturedCollections
import com.volleylord.core.ui.components.NetworkStub
import com.volleylord.core.ui.components.NoResultsStub
import com.volleylord.core.ui.components.AppendErrorItem
import com.volleylord.core.ui.components.shimmerEffect
import com.volleylord.core.ui.components.SearchBar
import com.volleylord.feature.photos.photos.components.PhotoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    viewModel: PhotoListViewModel,
    onPhotoClick: (Int) -> Unit,
    onNavigateToBookmarks: () -> Unit = {},
    selectedTab: Int = 0
) {
    val lazyPagingItems = viewModel.photos.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val homeIconActiveResId = context.resources.getIdentifier("home_button_active", "drawable", context.packageName)
    val homeIconInactiveResId = context.resources.getIdentifier("home_button_inactive", "drawable", context.packageName)
    val bookmarkIconActiveResId = context.resources.getIdentifier("bookmark_active", "drawable", context.packageName)
    val bookmarkIconInactiveResId = context.resources.getIdentifier("bookmark_inactive", "drawable", context.packageName)
    val searchIconResId = context.resources.getIdentifier("search_bar_icon", "drawable", context.packageName)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = com.volleylord.core.ui.theme.White,
        bottomBar = {
            com.volleylord.core.ui.components.BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tabIndex ->
                    if (tabIndex == 1) {
                        onNavigateToBookmarks()
                    }
                },
                homeIconActiveResId = if (homeIconActiveResId != 0) homeIconActiveResId else android.R.drawable.ic_menu_recent_history,
                homeIconInactiveResId = if (homeIconInactiveResId != 0) homeIconInactiveResId else android.R.drawable.ic_menu_recent_history,
                bookmarkIconActiveResId = if (bookmarkIconActiveResId != 0) bookmarkIconActiveResId else android.R.drawable.star_big_on,
                bookmarkIconInactiveResId = if (bookmarkIconInactiveResId != 0) bookmarkIconInactiveResId else android.R.drawable.star_big_off
            )
        }
    ) { paddingValues ->
        PhotoListContent(
            lazyPagingItems = lazyPagingItems,
            uiState = uiState,
            onPhotoClick = onPhotoClick,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onCollectionClick = viewModel::selectCollection,
            onLoadPopularPhotos = viewModel::loadPopularPhotos,
            searchIconResId = searchIconResId,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun rememberPagingState(items: LazyPagingItems<*>): Pair<PagingState, Boolean> {
    val refreshState = items.loadState.refresh
    val isNetworkError = refreshState is LoadState.Error &&
        NetworkErrorUtils.isNetworkError(refreshState.error)

    val pagingState = when {
        refreshState is LoadState.Loading && items.itemCount == 0 -> PagingState.Loading
        refreshState is LoadState.Error -> PagingState.Error(refreshState.error)
        items.itemCount == 0 && refreshState is LoadState.NotLoading -> PagingState.Empty
        else -> PagingState.Data
    }

    return Pair(pagingState, isNetworkError)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoListContent(
    lazyPagingItems: LazyPagingItems<Photo>,
    uiState: PhotoListUiState,
    onPhotoClick: (Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCollectionClick: (Collection) -> Unit,
    onLoadPopularPhotos: () -> Unit,
    searchIconResId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state = rememberPullToRefreshState()
    val (pagingState, isNetworkError) = rememberPagingState(lazyPagingItems)
    val isRefreshing =
        lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount > 0

    LaunchedEffect(pagingState, lazyPagingItems.itemCount) {
        if (pagingState is PagingState.Error &&
            lazyPagingItems.itemCount > 0 &&
            isNetworkError) {
            Toast.makeText(
                context,
                context.getString(R.string.error_network_no_connection),
                Toast.LENGTH_SHORT
            ).show()
        } else if (pagingState is PagingState.Error &&
            lazyPagingItems.itemCount == 0 &&
            !isNetworkError) {
            val errorMessage = NetworkErrorUtils.getUserFriendlyMessage(pagingState.throwable)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(lazyPagingItems.loadState.append) {
        val appendState = lazyPagingItems.loadState.append
        if (appendState is LoadState.Error && lazyPagingItems.itemCount > 0) {
            val errorMessage = if (NetworkErrorUtils.isNetworkError(appendState.error)) {
                context.getString(R.string.error_network_unstable)
            } else {
                NetworkErrorUtils.getUserFriendlyMessage(appendState.error)
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { lazyPagingItems.refresh() },
        modifier = modifier.fillMaxSize(),
        state = state
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(com.volleylord.core.ui.theme.White)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearchClick = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        onSearchQueryChange(uiState.searchQuery)
                    }
                },
                searchIconResId = if (searchIconResId != 0) searchIconResId else android.R.drawable.ic_menu_search,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = com.volleylord.core.ui.theme.Spacing.searchBarTop,
                        start = com.volleylord.core.ui.theme.Spacing.horizontal,
                        end = com.volleylord.core.ui.theme.Spacing.horizontal
                    )
            )

            val isLoading = lazyPagingItems.loadState.refresh is LoadState.Loading
            val isSearching = uiState.searchQuery.isNotEmpty()
            if (isLoading && isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = com.volleylord.core.ui.theme.Spacing.featuredCollectionsTop,
                            start = com.volleylord.core.ui.theme.Spacing.horizontal,
                            end = com.volleylord.core.ui.theme.Spacing.horizontal
                        ),
                    color = com.volleylord.core.ui.theme.PrimaryRed,
                    trackColor = com.volleylord.core.ui.theme.LightGray
                )
            }

            val showHeaders = !isSearching || (isNetworkError && lazyPagingItems.itemCount == 0)

            if (showHeaders) {
                FeaturedCollections(
                    collections = uiState.featuredCollections,
                    onCollectionClick = onCollectionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = com.volleylord.core.ui.theme.Spacing.featuredCollectionsTop,
                            start = com.volleylord.core.ui.theme.Spacing.horizontal,
                            end = com.volleylord.core.ui.theme.Spacing.horizontal
                        )
                )

                if (isLoading && !isSearching) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 8.dp,
                                start = com.volleylord.core.ui.theme.Spacing.horizontal,
                                end = com.volleylord.core.ui.theme.Spacing.horizontal
                            ),
                        color = com.volleylord.core.ui.theme.PrimaryRed,
                        trackColor = com.volleylord.core.ui.theme.LightGray
                    )
                }
            }

            PagingContent(
                pagingState = pagingState,
                isNetworkError = isNetworkError,
                lazyPagingItems = lazyPagingItems,
                uiState = uiState,
                onPhotoClick = onPhotoClick,
                onLoadPopularPhotos = onLoadPopularPhotos,
                onSearchQueryChange = onSearchQueryChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PagingContent(
    pagingState: PagingState,
    isNetworkError: Boolean,
    lazyPagingItems: LazyPagingItems<Photo>,
    uiState: PhotoListUiState,
    onPhotoClick: (Int) -> Unit,
    onLoadPopularPhotos: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (pagingState) {
        is PagingState.Error -> {
            if (isNetworkError && lazyPagingItems.itemCount == 0) {
                NetworkStub(
                    onTryAgainClick = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            lazyPagingItems.refresh()
                        } else {
                            lazyPagingItems.refresh()
                        }
                    },
                    modifier = modifier
                )
            } else {
                PhotoGrid(
                    lazyPagingItems = lazyPagingItems,
                    onPhotoClick = onPhotoClick,
                    modifier = modifier
                )
            }
        }

        is PagingState.Empty -> NoResultsStub(
            onExploreClick = onLoadPopularPhotos,
            onTextClick = { onSearchQueryChange("") },
            modifier = modifier
        )
        is PagingState.Data -> PhotoGrid(
            lazyPagingItems = lazyPagingItems,
            onPhotoClick = onPhotoClick,
            modifier = modifier
        )

        else -> {}
    }
}

@Composable
private fun PhotoGrid(
    lazyPagingItems: LazyPagingItems<Photo>,
    onPhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = staggeredGridState,
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = com.volleylord.core.ui.theme.Spacing.imagesGridTop,
                start = com.volleylord.core.ui.theme.Spacing.horizontal,
                end = com.volleylord.core.ui.theme.Spacing.horizontal
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
                PhotoItem(
                    photo = photo,
                    onClick = { onPhotoClick(photo.id) },
                    modifier = Modifier.aspectRatio(aspectRatio)
                )
            } else {
                ShimmerPhotoItem(
                    modifier = Modifier.aspectRatio(1f)
                )
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

            else -> { }
        }
    }
}

private fun calculateAspectRatio(width: Int?, height: Int?): Float {
    return if (width != null && height != null && width > 0 && height > 0) {
        width.toFloat() / height.toFloat()
    } else {
        1f
    }
}


private sealed class PagingState {
    object Loading : PagingState()
    data class Error(val throwable: Throwable) : PagingState()
    object Empty : PagingState()
    object Data : PagingState()
}

@Composable
private fun ShimmerPhotoItem(
    modifier: Modifier = Modifier
) {
    val shimmerSize = 120.dp
    Card(
        modifier = modifier
            .size(shimmerSize)
            .shimmerEffect(shimmerSize)
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}


