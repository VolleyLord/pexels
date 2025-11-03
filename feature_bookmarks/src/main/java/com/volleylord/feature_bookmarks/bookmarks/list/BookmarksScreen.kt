package com.volleylord.feature_bookmarks.bookmarks.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.volleylord.common.R
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White

/**
 * Bookmarks screen displaying saved photos.
 *
 * @param viewModel The ViewModel for managing bookmarked photos.
 * @param onPhotoClick Callback invoked when a photo is clicked, passing the photo ID.
 * @param onExploreClick Callback invoked when "Explore" button is clicked to navigate to Home.
 */
@Composable
fun BookmarksScreen(
  viewModel: BookmarksViewModel = hiltViewModel(),
  onPhotoClick: (Int) -> Unit,
  onExploreClick: () -> Unit
) {
  val lazyPagingItems = viewModel.photos.collectAsLazyPagingItems()

  val context = LocalContext.current
  val bookmarkIconActiveResId = context.resources.getIdentifier("bookmark_active", "drawable", context.packageName)
  val bookmarkIconInactiveResId = context.resources.getIdentifier("bookmark_inactive", "drawable", context.packageName)
  val homeIconActiveResId = context.resources.getIdentifier("home_button_active", "drawable", context.packageName)
  val homeIconInactiveResId = context.resources.getIdentifier("home_button_inactive", "drawable", context.packageName)

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = White,
    bottomBar = {
      com.volleylord.core.ui.components.BottomBar(
        selectedTab = 1, // Bookmarks tab is selected
        onTabSelected = { tabIndex ->
          if (tabIndex == 0) {
            onExploreClick()
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
      lazyPagingItems = lazyPagingItems,
      onPhotoClick = onPhotoClick,
      onExploreClick = onExploreClick,
      modifier = Modifier.padding(paddingValues)
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

  // "Bookmarks" title dimensions from Figma
  val titleWidth = (97 * scale).dp
  val titleHeight = (23 * scale).dp
  val titleTop = (69 * scale).dp
  val titleLeft = (140 * scale).dp
  val titleFontSize = (18 * scale).sp

  // Images container dimensions
  val imagesContainerTop = (130 * scale).dp
  val imagesContainerLeft = (24 * scale).dp
  val imagesContainerWidth = (327 * scale).dp

  // Image dimensions - left column (constant)
  val leftImageWidth = (155 * scale).dp
  val leftImageHeight = (250 * scale).dp

  // Image dimensions - right column (constant)
  val rightImageWidth = (155 * scale).dp
  val rightImageHeight = (170 * scale).dp

  // Author selection dimensions
  val authorSelectionWidth = (155 * scale).dp
  val authorSelectionHeight = (33 * scale).dp
  val authorTextWidth = (155 * scale).dp
  val authorTextHeight = (19 * scale).dp
  val authorFontSize = (14 * scale).sp

  val isLoading = lazyPagingItems.loadState.refresh is LoadState.Loading
  val isEmpty = lazyPagingItems.loadState.refresh !is LoadState.Loading && lazyPagingItems.itemCount == 0

  Box(modifier = modifier.fillMaxSize()) {
    // "Bookmarks" title
    Text(
      text = stringResource(R.string.bookmarks_screen_title),
      fontSize = titleFontSize,
      fontWeight = FontWeight.Bold,
      color = Color.Black,
      modifier = Modifier
        .offset(x = titleLeft, y = titleTop)
        .width(titleWidth)
        .height(titleHeight)
    )

    // Progress bar between title and images
    if (isLoading) {
      LinearProgressIndicator(
        modifier = Modifier
          .fillMaxWidth()
          .offset(y = titleTop + titleHeight + 8.dp)
          .padding(horizontal = 24.dp),
        color = PrimaryRed,
        trackColor = LightGray
      )
    }

    // Content: Empty state or Photo grid
    if (isEmpty) {
      // Empty bookmarks stub
      EmptyBookmarksStub(
        onExploreClick = onExploreClick,
        modifier = Modifier
          .fillMaxSize()
          .offset(y = imagesContainerTop)
      )
    } else {
      // Bookmarks grid
      LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
          .fillMaxSize()
          .offset(x = imagesContainerLeft, y = imagesContainerTop)
          .width(imagesContainerWidth),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy((17 * scale).dp), // spacing between columns (196 - 155 - 24 = 17)
        // verticalArrangement = Arrangement.spacedBy(8.dp)

      ) {
        itemsIndexed(lazyPagingItems.itemSnapshotList.items) { index, photo ->
          if (photo != null) {
            val isLeftColumn = index % 2 == 0
            val imageWidth = if (isLeftColumn) leftImageWidth else rightImageWidth
            val imageHeight = if (isLeftColumn) leftImageHeight else rightImageHeight
            val aspectRatio = calculateAspectRatio(photo.width, photo.height)

            BookmarkPhotoItem(
              photo = photo,
              onClick = { onPhotoClick(photo.id) },
              imageWidth = imageWidth,
              imageHeight = imageHeight,
              authorSelectionWidth = authorSelectionWidth,
              authorSelectionHeight = authorSelectionHeight,
              authorTextWidth = authorTextWidth,
              authorTextHeight = authorTextHeight,
              authorFontSize = authorFontSize,
              modifier = Modifier
                .width(imageWidth)
                .aspectRatio(aspectRatio)
                .padding(bottom = 8.dp)
            )
          } else {
            // Shimmer placeholder
            ShimmerBookmarkItem(
              modifier = Modifier
                .width(if (index % 2 == 0) leftImageWidth else rightImageWidth)
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
                  modifier = Modifier.size(32.dp),
                  strokeWidth = 3.dp,
                  color = PrimaryRed
                )
              }
            }
          }
          else -> {}
        }
      }
    }
  }
}

/**
 * Calculates the aspect ratio of the photo based on its width and height.
 */
private fun calculateAspectRatio(width: Int?, height: Int?): Float {
  return if (width != null && height != null && width > 0 && height > 0) {
    width.toFloat() / height.toFloat()
  } else {
    1f // Default to square if dimensions are invalid
  }
}

/**
 * Photo item component for bookmarks with author label.
 */
@Composable
private fun BookmarkPhotoItem(
  photo: Photo,
  onClick: () -> Unit,
  imageWidth: androidx.compose.ui.unit.Dp,
  imageHeight: androidx.compose.ui.unit.Dp,
  authorSelectionWidth: androidx.compose.ui.unit.Dp,
  authorSelectionHeight: androidx.compose.ui.unit.Dp,
  authorTextWidth: androidx.compose.ui.unit.Dp,
  authorTextHeight: androidx.compose.ui.unit.Dp,
  authorFontSize: androidx.compose.ui.unit.TextUnit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Photo image with click effect
    Box(
      modifier = Modifier
        .width(imageWidth)
        .height(imageHeight)
        .clip(RoundedCornerShape(8.dp))
        .clickable(onClick = onClick)
    ) {
      AsyncImage(
        model = photo.tinyThumbnailUrl ?: photo.thumbnailUrl,
        contentDescription = photo.alt ?: "Photo",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    // Author selection
    Box(
      modifier = Modifier
        .width(authorSelectionWidth)
        .height(authorSelectionHeight)
        .background(
          color = Color(0x66000000), // #00000066 (40% opacity)
          shape = RoundedCornerShape(8.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = photo.photographer ?: "",
        fontSize = authorFontSize,
        fontWeight = FontWeight.Normal,
        color = White,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .width(authorTextWidth)
          .height(authorTextHeight)
      )
    }
  }
}

/**
 * Empty bookmarks stub component.
 */
@Composable
private fun EmptyBookmarksStub(
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val configuration = LocalConfiguration.current
  val baseScreenWidth = 375
  val screenWidth = configuration.screenWidthDp
  val scale = screenWidth.toFloat() / baseScreenWidth

  // Empty stub dimensions from Figma
  val stubWidth = (203 * scale).dp
  val stubHeight = (53 * scale).dp
  val stubTop = (380 * scale).dp
  val stubLeft = (86 * scale).dp

  // Text dimensions
  val textWidth = (203 * scale).dp
  val textHeight = (18 * scale).dp
  val textFontSize = (14 * scale).sp
  val textTop = (380 * scale).dp

  // Explore button dimensions
  val exploreButtonWidth = (64 * scale).dp
  val exploreButtonHeight = (23 * scale).dp
  val exploreButtonTop = (410 * scale).dp
  val exploreButtonLeft = (156 * scale).dp
  val exploreButtonFontSize = (16 * scale).sp

  Column(
    modifier = modifier
      .offset(x = stubLeft, y = stubTop)
      .width(stubWidth)
      .height(stubHeight),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // "You haven't saved anything yet" text
    Text(
      text = stringResource(R.string.bookmarks_empty_title),
      fontSize = textFontSize,
      fontWeight = FontWeight.Normal,
      color = Color(0xFF333333), // #333333
      textAlign = TextAlign.Center,
      modifier = Modifier
        .width(textWidth)
        .height(textHeight)
    )

    // "Explore" button
    Text(
      text = stringResource(R.string.bookmarks_empty_explore),
      fontSize = exploreButtonFontSize,
      fontWeight = FontWeight.Bold,
      color = PrimaryRed,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .offset(x = exploreButtonLeft - stubLeft)
        .width(exploreButtonWidth)
        .height(exploreButtonHeight)
        .clickable(onClick = onExploreClick)
    )
  }
}

/**
 * Shimmer placeholder for loading bookmarks.
 */
@Composable
private fun ShimmerBookmarkItem(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(LightGray)
  ) {
    // Shimmer effect would go here
  }
}

