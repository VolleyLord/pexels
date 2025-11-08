package com.volleylord.feature_bookmarks.presentation.bookmarks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.volleylord.common.R
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.components.BottomBar
import com.volleylord.core.ui.components.shimmerEffect
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White
import com.volleylord.feature.photos.presentation.home.utils.rememberBottomBarVisibilityOnScroll
import com.volleylord.feature_bookmarks.bookmarks.components.BookmarkPhotoItem
import com.volleylord.feature_bookmarks.bookmarks.components.EmptyBookmarksStub

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = hiltViewModel(),
    navBackStackEntryId: String = "",
    onPhotoClick: (Int) -> Unit,
    onExploreClick: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
  val bookmarks = viewModel.bookmarks.collectAsLazyPagingItems()
  val context = LocalContext.current

  LaunchedEffect(navBackStackEntryId) {
    if (navBackStackEntryId.isNotEmpty()) {
      bookmarks.refresh()
    }
  }


  val homeIconActiveResId = context.resources.getIdentifier("home_button_active", "drawable", context.packageName)
  val homeIconInactiveResId = context.resources.getIdentifier("home_button_inactive", "drawable", context.packageName)
  val bookmarkIconActiveResId = context.resources.getIdentifier("bookmark_active", "drawable", context.packageName)
  val bookmarkIconInactiveResId = context.resources.getIdentifier("bookmark_inactive", "drawable", context.packageName)

  val bottomBarScroll = rememberBottomBarVisibilityOnScroll()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = White,
    bottomBar = {
      AnimatedVisibility(
        visible = bottomBarScroll.isVisibleState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
      ) {
        BottomBar(
          selectedTab = 1,
          onTabSelected = { tabIndex ->
            if (tabIndex == 0) {
              onNavigateToHome()
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
    BookmarksContent(
      lazyPagingItems = bookmarks,
      onPhotoClick = onPhotoClick,
      onExploreClick = onExploreClick,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .nestedScroll(bottomBarScroll.connection)
    )
  }
}

@Composable
private fun BookmarksContent(
    lazyPagingItems: LazyPagingItems<Photo>,
    onPhotoClick: (Int) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = lazyPagingItems.loadState.refresh is LoadState.Loading
    val isEmpty = lazyPagingItems.itemCount == 0 &&
            lazyPagingItems.loadState.refresh is LoadState.NotLoading &&
            lazyPagingItems.loadState.append !is LoadState.Loading

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))  // Spacing from top to "Bookmarks" title

        Text(
            text = stringResource(R.string.bookmarks_screen_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))  // Spacing between title and image grid

        val isInitialLoading = isLoading && lazyPagingItems.itemCount == 0
        val isRefreshing = isLoading && lazyPagingItems.itemCount > 0

        if (isInitialLoading || isRefreshing) {
            com.volleylord.core.ui.components.AppLinearProgressIndicator(
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        when {
            isEmpty -> {
                EmptyBookmarksStub(
                    onExploreClick = onExploreClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
            else -> {
                BookmarksGrid(
                    lazyPagingItems = lazyPagingItems,
                    onPhotoClick = onPhotoClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BookmarksGrid(
  lazyPagingItems: LazyPagingItems<Photo>,
  onPhotoClick: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(2),
    modifier = modifier
      .fillMaxSize(),
    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
    verticalItemSpacing = 12.dp
  ) {
    items(
      count = lazyPagingItems.itemCount,
      key = { index -> lazyPagingItems[index]?.id ?: index }
    ) { index ->
      val photo = lazyPagingItems[index]

      if (photo != null) {
        val aspectRatio = if (photo.width != null && photo.height != null && photo.height != 0) {
          photo.width!!.toFloat() / photo.height!!.toFloat()
        } else 1f

        BookmarkPhotoItem(
          photo = photo,
          onClick = { onPhotoClick(photo.id) },
          modifier = Modifier.aspectRatio(aspectRatio)
        )
      } else {
        ShimmerBookmarkPhotoItem(
          modifier = Modifier.aspectRatio(1f)
        )
      }
    }

  }
}

@Composable
private fun ShimmerBookmarkPhotoItem(
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .shimmerEffect(120.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      shape = RoundedCornerShape(20.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize())
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(33.dp)
        .background(
          color = PrimaryRed,
          shape = RoundedCornerShape(
            bottomStart = 20.dp,
            bottomEnd = 20.dp
          )
        ),
      contentAlignment = Alignment.Center
    ) {
    }
  }
}


