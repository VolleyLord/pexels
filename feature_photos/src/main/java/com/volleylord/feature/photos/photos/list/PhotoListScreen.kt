package com.volleylord.feature.photos.photos.list

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.volleylord.core.ui.components.SearchBar
import com.volleylord.feature.photos.photos.components.PhotoItem

/**
 * Represents the paging state for the photo list screen.
 */
private sealed class PagingState {
    /** Indicates the screen is loading for the first time. */
    object Loading : PagingState()
    /** Indicates an error occurred while loading photos. */
    data class Error(val throwable: Throwable) : PagingState()
    /** Indicates no photos are available to display. */
    object Empty : PagingState()
    /** Indicates photos are available to display. */
    object Data : PagingState()
}

/**
 * Composable function that renders the photo list screen.
 *
 * @param viewModel The ViewModel for managing photo list data.
 * @param onPhotoClick Callback invoked when a photo is clicked, passing the photo ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    viewModel: PhotoListViewModel,
    onPhotoClick: (Int) -> Unit
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
        containerColor = com.volleylord.core.ui.theme.White, // Белый фон
        bottomBar = {
            com.volleylord.core.ui.components.BottomBar(
                selectedTab = 0, // TODO: manage through state/navigation
                onTabSelected = { /* TODO: navigate to tab */ },
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


/**
 * Determines the paging state based on the load state and item count.
 * Also tracks network errors for displaying appropriate UI.
 *
 * @param items The [LazyPagingItems] containing the photos.
 * @return The current [PagingState] and whether it's a network error.
 */
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

/**
 * Renders the content of the photo list screen with pull-to-refresh functionality.
 *
 * @param lazyPagingItems The [LazyPagingItems] containing the photos.
 * @param uiState The UI state containing search query and collections.
 * @param onPhotoClick Callback invoked when a photo is clicked.
 * @param onSearchQueryChange Callback when search query changes.
 * @param onCollectionClick Callback when a collection is clicked.
 * @param modifier The modifier for the composable.
 */
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

    // Show Toast when network error occurs and cached data is available
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
            // Non-network error with no data - show user-friendly message
            val errorMessage = NetworkErrorUtils.getUserFriendlyMessage(pagingState.throwable)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Show Toast for append errors when there's existing data
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
                    // Trigger search when IME action is pressed
                    // Search already happens automatically on text change,
                    // but this ensures it works when user presses search button
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

            // Show progress bar below SearchBar if searching (when headers are hidden)
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

            // Show FeaturedCollections when not searching OR when showing Network Stub
            // (Network Stub means no cached data, but headers should still be visible)
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

                // Show progress bar below FeaturedCollections if loading and headers are visible
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

            // Photo Grid
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

/**
 * Renders the content based on the paging state.
 *
 * @param pagingState The current [PagingState].
 * @param isNetworkError Whether the error is network-related.
 * @param lazyPagingItems The [LazyPagingItems] containing the photos.
 * @param uiState The UI state to determine retry action (popular vs search).
 * @param onPhotoClick Callback invoked when a photo is clicked, passing the photo ID.
 * @param onLoadPopularPhotos Callback to load popular curated photos.
 * @param onSearchQueryChange Callback to clear search query or retry search.
 * @param modifier The modifier for the composable.
 */
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
        is PagingState.Loading -> LoadingState(modifier)
        is PagingState.Error -> {
            // Show Network Stub if network error and no cached data
            if (isNetworkError && lazyPagingItems.itemCount == 0) {
                NetworkStub(
                    onTryAgainClick = {
                        // Retry based on current query: popular photos or search
                        if (uiState.searchQuery.isNotEmpty()) {
                            // Retry search with same query - use refresh to force reload
                            lazyPagingItems.refresh()
                        } else {
                            // Retry popular photos - refresh will trigger popular photos load
                            lazyPagingItems.refresh()
                        }
                    },
                    modifier = modifier
                )
            } else if (lazyPagingItems.itemCount == 0) {
                // Non-network error with no data - show generic error state
                ErrorState(
                    error = pagingState.throwable,
                    onRetry = { lazyPagingItems.retry() },
                    modifier = modifier
                )
            } else {
                // Error but cached data exists - show cached data
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
    }
}

/**
 * Renders the loading state for the photo list screen.
 *
 * @param modifier The modifier for the composable.
 */
@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.photo_list_loading_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Renders the error state for the photo list screen.
 *
 * @param error The error that occurred.
 * @param onRetry Callback invoked to retry loading the photos.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun ErrorState(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.error_photo_list_load_failed),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = error.message ?: stringResource(R.string.error_generic),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.common_button_retry))
        }
    }
}

/**
 * Renders the photo grid with lazy-loaded photos.
 *
 * @param lazyPagingItems The [LazyPagingItems] containing the photos.
 * @param onPhotoClick Callback invoked when a photo is clicked, passing the photo ID.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun PhotoGrid(
    lazyPagingItems: LazyPagingItems<Photo>,
    onPhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2), // 2 columns
        state = staggeredGridState,
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = com.volleylord.core.ui.theme.Spacing.imagesGridTop,
                start = com.volleylord.core.ui.theme.Spacing.horizontal,
                end = com.volleylord.core.ui.theme.Spacing.horizontal
            ),
        horizontalArrangement = Arrangement.spacedBy(10.dp), // gap horiz
        verticalItemSpacing = 10.dp // gap vert
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = { index -> lazyPagingItems.peek(index)?.id ?: index }
        ) { index ->
            val photo = lazyPagingItems[index]

            if (photo != null) {
                // true aspect ratio
                val aspectRatio = calculateAspectRatio(photo.width, photo.height)
                PhotoItem(
                    photo = photo,
                    onClick = { onPhotoClick(photo.id) },
                    modifier = Modifier.aspectRatio(aspectRatio)
                )
            } else {
                // shimmer
                ShimmerPhotoItem(
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }

        // Empty state is handled by PagingContent at a higher level

        // Handle append loading states
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

            else -> { /* No additional content needed */
            }
        }
    }
}

/**
 * Calculates the aspect ratio of the photo based on its width and height.
 *
 * @param width The width of the photo, if available.
 * @param height The height of the photo, if available.
 * @return The aspect ratio as a float, or 1f if width or height is invalid.
 */
private fun calculateAspectRatio(width: Int?, height: Int?): Float {
    return if (width != null && height != null && width > 0 && height > 0) {
        width.toFloat() / height.toFloat()
    } else {
        1f // Default to square if dimensions are invalid
    }
}

/**
 * Renders the "No results found" stub component according to Figma specifications.
 *
 * @param onExploreClick Callback when "Explore" button is clicked.
 * @param onTextClick Callback when "No results found" text is clicked.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun NoResultsStub(
    onExploreClick: () -> Unit,
    onTextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val baseScreenWidth = 375
    val screenWidth = configuration.screenWidthDp
    val scale = screenWidth.toFloat() / baseScreenWidth

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // "No results found" text - clickable to clear search
        Text(
            text = "No results found",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Medium, // 500
                lineHeight = (14 * scale).sp, // 100% line height
                letterSpacing = 0.sp // 0% letter spacing
            ),
            textAlign = TextAlign.Center,
            color = com.volleylord.core.ui.theme.TextSecondary, // #333333
            modifier = Modifier
                .clickable(onClick = onTextClick)
                .padding(bottom = 8.dp)
        )

        // "Explore" button
        Text(
            text = "Explore",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold, // 700
                lineHeight = (18 * scale).sp, // 100% line height
                letterSpacing = 0.sp // 0% letter spacing
            ),
            textAlign = TextAlign.Center,
            color = com.volleylord.core.ui.theme.PrimaryRed,
            modifier = Modifier.clickable(onClick = onExploreClick)
        )
    }
}

/**
 * Renders an error item for failed append operations in the photo grid.
 *
 * @param error The error that occurred.
 * @param onRetry Callback invoked to retry the append operation.
 */
@Composable
private fun AppendErrorItem(
    error: Throwable,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error.message ?: stringResource(R.string.error_photo_list_append_failed),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.common_button_retry))
        }
    }
}

/**
 * Renders a shimmer effect for a loading photo item.
 *
 * @param modifier The modifier for the composable.
 */
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

/**
 * Applies a shimmer effect to a composable for loading states.
 *
 * @param size The size of the shimmer effect.
 * @return The modified [Modifier] with the shimmer effect.
 */
private fun Modifier.shimmerEffect(size: Dp): Modifier = composed {
    val sizePx = with(LocalDensity.current) { size.toPx() }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * sizePx,
        targetValue = 2 * sizePx,
        animationSpec = infiniteRepeatable(tween(1000)),
        label = "shimmer_offset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + sizePx, sizePx)
        )
    )
}