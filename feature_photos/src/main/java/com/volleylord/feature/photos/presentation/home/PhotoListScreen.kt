package com.volleylord.feature.photos.presentation.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.volleylord.common.R
import com.volleylord.core.core.network.NetworkErrorUtils
import com.volleylord.core.domain.models.Collection
import com.volleylord.core.domain.models.Photo
import com.volleylord.feature.photos.presentation.home.components.HomeContent
import com.volleylord.feature.photos.presentation.home.components.HomeHeader
import com.volleylord.feature.photos.presentation.home.components.toPagingTag
import com.volleylord.feature.photos.presentation.home.utils.rememberBottomBarVisibilityOnScroll

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

    // Bottom bar show/hide on scroll
    val bottomBarScroll = rememberBottomBarVisibilityOnScroll()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = com.volleylord.core.ui.theme.White,
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarScroll.isVisibleState.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
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
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(bottomBarScroll.connection)
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

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
            val isLoading = lazyPagingItems.loadState.refresh is LoadState.Loading
            val isSearching = uiState.searchQuery.isNotEmpty()
            val showHeaders = !isSearching || (isNetworkError && lazyPagingItems.itemCount == 0)

            if (showHeaders) {
                HomeHeader(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { if (uiState.searchQuery.isNotEmpty()) onSearchQueryChange(uiState.searchQuery) },
                    searchIconResId = if (searchIconResId != 0) searchIconResId else android.R.drawable.ic_menu_search,
                    featuredCollections = uiState.featuredCollections,
                    onCollectionClick = onCollectionClick,
                    showTopProgress = isLoading && isSearching,
                    showBelowFeaturedProgress = isLoading && !isSearching,
                    isLandscape = isLandscape
                )
            } else {
                // when searching, show only searchBar (no headers)
                HomeHeader(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { if (uiState.searchQuery.isNotEmpty()) onSearchQueryChange(uiState.searchQuery) },
                    searchIconResId = if (searchIconResId != 0) searchIconResId else android.R.drawable.ic_menu_search,
                    featuredCollections = emptyList(),
                    onCollectionClick = {},
                    showTopProgress = isLoading && isSearching,
                    showBelowFeaturedProgress = false,
                    isLandscape = isLandscape
                )
            }

            val pagingTag = toPagingTag(pagingState)
            HomeContent(
                pagingStateTag = pagingTag,
                isNetworkError = isNetworkError,
                lazyPagingItems = lazyPagingItems,
                onPhotoClick = onPhotoClick,
                onLoadPopularPhotos = onLoadPopularPhotos,
                onSearchQueryChange = onSearchQueryChange,
                isLandscape = isLandscape,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
