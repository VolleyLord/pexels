package com.volleylord.feature_bookmarks.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.volleylord.common.R
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.components.BottomBar
import com.volleylord.core.ui.components.shimmerEffect
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White
import com.volleylord.feature_bookmarks.presentation.bookmarks.BookmarksViewModel
import com.volleylord.feature_bookmarks.bookmarks.components.BookmarkPhotoItem
import com.volleylord.feature_bookmarks.bookmarks.components.EmptyBookmarksStub

@Composable
fun BookmarksScreen(
  viewModel: BookmarksViewModel = hiltViewModel(),
  onPhotoClick: (Int) -> Unit,
  onExploreClick: () -> Unit,
  onNavigateToHome: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val bookmarks = viewModel.bookmarks.collectAsLazyPagingItems()
  val context = LocalContext.current

  val homeIconActiveResId = context.resources.getIdentifier("home_button_active", "drawable", context.packageName)
  val homeIconInactiveResId = context.resources.getIdentifier("home_button_inactive", "drawable", context.packageName)
  val bookmarkIconActiveResId = context.resources.getIdentifier("bookmark_active", "drawable", context.packageName)
  val bookmarkIconInactiveResId = context.resources.getIdentifier("bookmark_inactive", "drawable", context.packageName)

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = White,
    bottomBar = {
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
  ) { paddingValues ->
    BookmarksContent(
      lazyPagingItems = bookmarks,
      onPhotoClick = onPhotoClick,
      onExploreClick = onExploreClick,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .statusBarsPadding()
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
    val configuration = LocalConfiguration.current
    val baseScreenWidth = 375
    val screenWidth = configuration.screenWidthDp
    val scale = screenWidth.toFloat() / baseScreenWidth

    val titleTop = (69 * scale).dp
    val titleLeft = (140 * scale).dp
    val titleWidth = (130 * scale).dp
    val imagesTop = (130 * scale).dp
    val imagesLeft = (24 * scale).dp
    val imagesWidth = (327 * scale).dp

    val isLoading = lazyPagingItems.loadState.refresh is LoadState.Loading
    val isEmpty = lazyPagingItems.itemCount == 0 &&
            lazyPagingItems.loadState.refresh is LoadState.NotLoading &&
            lazyPagingItems.loadState.append !is LoadState.Loading

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(titleTop))

        Box(
            modifier = Modifier
                .offset(x = titleLeft)
                .width(titleWidth)
        ) {
            Text(
                text = stringResource(R.string.bookmarks_screen_title),
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height((imagesTop - titleTop - 18.dp).coerceAtLeast(0.dp)))

        if (isLoading && !isEmpty) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(imagesWidth)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
                color = PrimaryRed,
                trackColor = LightGray
            )
        }

        Box(
            modifier = Modifier
                .width(imagesWidth)
                .align(Alignment.CenterHorizontally)
                .fillMaxHeight()
        ) {
            when {
                isEmpty -> {
                    EmptyBookmarksStub(
                        onExploreClick = onExploreClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    BookmarksGrid(
                        lazyPagingItems = lazyPagingItems,
                        onPhotoClick = onPhotoClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
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
  val configuration = LocalConfiguration.current
  val baseScreenWidth = 375
  val screenWidth = configuration.screenWidthDp
  val scale = screenWidth.toFloat() / baseScreenWidth

  val contentWidth = (327 * scale).dp
  val gap = 10.dp
  val itemWidthPx = with(LocalDensity.current) { (contentWidth - gap).toPx() / 2f }
  val itemWidth = with(LocalDensity.current) { itemWidthPx.toDp() }

  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(2),
    modifier = modifier,
    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(gap),
    verticalItemSpacing = gap
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
          modifier = Modifier
            .width(itemWidth)
            .aspectRatio(aspectRatio)
        )
      } else {
        ShimmerBookmarkPhotoItem(
          modifier = Modifier
            .width(itemWidth)
            .aspectRatio(1f)
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
              modifier = Modifier.width(32.dp).height(32.dp),
              strokeWidth = 3.dp,
              color = PrimaryRed
            )
          }
        }
      }
      is LoadState.Error -> {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Error loading more",
              color = Color.Red
            )
          }
        }
      }
      else -> {}
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
      shape = RoundedCornerShape(8.dp)
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
            bottomStart = 8.dp,
            bottomEnd = 8.dp
          )
        ),
      contentAlignment = Alignment.Center
    ) {
    }
  }
}


