package com.volleylord.feature_bookmarks.bookmarks

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import com.volleylord.core.ui.components.BottomBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalConfiguration
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
import com.volleylord.core.ui.image.AsyncImageWithPlaceholder
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White
import com.volleylord.feature_bookmarks.bookmarks.components.BookmarkPhotoItem
import com.volleylord.feature_bookmarks.bookmarks.components.EmptyBookmarksStub

/**
 * Main screen for displaying bookmarked photos.
 * Based on Figma specs with adaptive sizing and pagination support.
 *
 * @param viewModel The ViewModel for managing bookmarks data.
 * @param onPhotoClick Callback invoked when a photo is clicked.
 * @param onExploreClick Callback invoked when "Explore" button is clicked in empty state.
 * @param modifier The modifier for the composable.
 */
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
  val imagesTop = (130 * scale).dp
  val imagesLeft = (24 * scale).dp
  val imagesWidth = (327 * scale).dp

  val isLoading = lazyPagingItems.loadState.refresh is LoadState.Loading
  val isEmpty = lazyPagingItems.itemCount == 0 && 
                lazyPagingItems.loadState.refresh !is LoadState.Loading &&
                lazyPagingItems.loadState.refresh !is LoadState.Error

  Column(
    modifier = modifier.fillMaxSize()
  ) {
    // Title: "Bookmarks"
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = titleTop, start = titleLeft)
    ) {
      Text(
        text = stringResource(R.string.bookmarks_screen_title),
        fontSize = (23 * scale).sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
      )
    }

    // Progress bar between title and images during loading
    if (isLoading && !isEmpty) {
      LinearProgressIndicator(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = imagesLeft, vertical = 8.dp),
        color = PrimaryRed,
        trackColor = LightGray
      )
    }

    // Content: Grid or Empty State
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = imagesTop, start = imagesLeft, end = imagesLeft)
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

  val imageWidth = (155 * scale).dp
  val gap = 10.dp

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
        val aspectRatio = if (photo.width != null && photo.height != null) {
          photo.width!!.toFloat() / photo.height!!.toFloat()
        } else {
          1f
        }

        BookmarkPhotoItem(
          photo = photo,
          onClick = { onPhotoClick(photo.id) },
          modifier = Modifier
            .width(imageWidth)
            .aspectRatio(aspectRatio)
        )
      } else {
        // Shimmer loading placeholder
        ShimmerBookmarkPhotoItem(
          modifier = Modifier
            .width(imageWidth)
            .aspectRatio(1f)
        )
      }
    }

    // Append loading state
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
  val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
  val shimmerTranslate by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1000f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = androidx.compose.animation.core.LinearEasing)
    ),
    label = "shimmer_translate"
  )

  val shimmerBrush = Brush.linearGradient(
    colors = listOf(
      Color.LightGray.copy(alpha = 0.6f),
      Color.LightGray.copy(alpha = 0.2f),
      Color.LightGray.copy(alpha = 0.6f)
    ),
    start = Offset(shimmerTranslate - 300f, shimmerTranslate - 300f),
    end = Offset(shimmerTranslate, shimmerTranslate)
  )

  Column(modifier = modifier) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(shimmerBrush)
          .clip(RoundedCornerShape(8.dp))
      )
    }

    // Author shimmer placeholder
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(33.dp)
        .padding(horizontal = 6.dp, vertical = 0.dp)
        .background(
          color = Color.LightGray.copy(alpha = 0.3f),
          shape = RoundedCornerShape(4.dp)
        )
    )
  }
}
