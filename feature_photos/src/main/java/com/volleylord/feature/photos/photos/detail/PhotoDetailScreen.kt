package com.volleylord.feature.photos.photos.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.theme.LightGray
import com.volleylord.core.ui.theme.PrimaryRed
import com.volleylord.core.ui.theme.White
import kotlin.math.max
import kotlin.math.min

@Composable
fun PhotoDetailScreen(
    photoId: Int,
    viewModel: PhotoDetailViewModel,
    onBackClick: () -> Unit,
    isFromBookmarks: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId, isFromBookmarks)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = White
    ) { paddingValues ->
        PhotoDetailContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onDownloadClick = { photo ->
                viewModel.downloadPhoto(photo, context)
            },
            onBookmarkClick = { photo ->
                viewModel.toggleBookmark(photo)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun PhotoDetailContent(
    uiState: PhotoDetailUiState,
    onBackClick: () -> Unit,
    onDownloadClick: (Photo) -> Unit,
    onBookmarkClick: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is PhotoDetailUiState.Loading -> {
            LoadingState(modifier = modifier.fillMaxSize())
        }

        is PhotoDetailUiState.Error -> {
            ErrorState(
                message = uiState.message,
                onRetry = { /* TODO: retry */ },
                onBackClick = onBackClick,
                modifier = modifier.fillMaxSize()
            )
        }

        is PhotoDetailUiState.Success -> {
            uiState.photo?.let { photo ->
                PhotoDetailSuccess(
                    photo = photo,
                    onBackClick = onBackClick,
                    onDownloadClick = { onDownloadClick(photo) },
                    onBookmarkClick = { onBookmarkClick(photo) },
                    isBookmarked = photo.liked == true,
                    modifier = modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryRed)
    }
}

@Composable
private fun ErrorState(
    message: String?,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message ?: "Error loading photo",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBackClick) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun PhotoDetailSuccess(
    photo: Photo,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    isBookmarked: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val baseScreenWidth = 375
    val baseScreenHeight = 812

    val baseTopBarHeight = 40
    val baseTopBarTop = 61
    val baseTopBarLeft = 24

    val baseBackButtonSize = 40
    val baseBackIconSize = 20
    val baseBackIconTop = 71
    val baseBackIconLeft = 34

    val baseAuthorTextWidth = 134
    val baseAuthorTextHeight = 23
    val baseAuthorTextTop = 69
    val baseAuthorTextLeft = 121

    val baseImageWidth = 327
    val baseImageHeight = 586
    val baseImageTop = 130
    val baseImageLeft = 24

    val baseActionsBarWidth = 327
    val baseActionsBarHeight = 48
    val baseActionsBarTop = 740
    val baseActionsBarLeft = 24

    val baseDownloadButtonWidth = 180
    val baseDownloadButtonHeight = 48
    val baseDownloadButtonLeft = 24

    val baseDownloadIconSize = 48
    val baseDownloadIconArrowSize = 12.67f
    val baseDownloadIconArrowTop = 17
    val baseDownloadIconArrowLeft = 18

    val baseDownloadTextWidth = 77.91f
    val baseDownloadTextHeight = 19.2f
    val baseDownloadTextTop = 15
    val baseDownloadTextLeft = 65

    val baseBookmarkButtonSize = 48
    val baseBookmarkButtonLeft = 303
    val baseBookmarkIconSize = 18
    val baseBookmarkIconTop = 14
    val baseBookmarkIconLeft = 15

    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val scale = minOf(
        screenWidth.toFloat() / baseScreenWidth,
        screenHeight.toFloat() / baseScreenHeight
    )

    val topBarHeight = (baseTopBarHeight * scale).dp
    val topBarTop = (baseTopBarTop * scale).dp
    val topBarLeft = (baseTopBarLeft * scale).dp

    val backButtonSize = (baseBackButtonSize * scale).dp
    val backIconSize = (baseBackIconSize * scale).dp

    val authorTextSize = (18 * scale).sp

    val imageWidth = (baseImageWidth * scale).dp
    val imageHeight = (baseImageHeight * scale).dp
    val imageTop = (baseImageTop * scale).dp
    val imageLeft = (baseImageLeft * scale).dp

    val actionsBarWidth = (baseActionsBarWidth * scale).dp
    val actionsBarHeight = (baseActionsBarHeight * scale).dp
    val actionsBarLeft = (baseActionsBarLeft * scale).dp

    val downloadButtonWidth = (baseDownloadButtonWidth * scale).dp
    val downloadButtonHeight = (baseDownloadButtonHeight * scale).dp
    val downloadIconSize = (baseDownloadIconSize * scale).dp
    val downloadIconArrowSize = (baseDownloadIconArrowSize * scale).dp

    val bookmarkButtonSize = (baseBookmarkButtonSize * scale).dp
    val bookmarkIconSize = (baseBookmarkIconSize * scale).dp

    Box(modifier = modifier.fillMaxSize()) {
        val context = LocalContext.current
        val backIconResId = context.resources.getIdentifier(
            "back_icon",
            "drawable",
            context.packageName
        )
        val downloadArrowResId = context.resources.getIdentifier(
            "download_arrow",
            "drawable",
            context.packageName
        )
        val bookmarkActiveResId = context.resources.getIdentifier(
            "bookmark_active",
            "drawable",
            context.packageName
        )
        val bookmarkInactiveResId = context.resources.getIdentifier(
            "bookmark_inactive",
            "drawable",
            context.packageName
        )

        CustomTopBar(
            authorName = photo.photographer ?: "",
            backIconResId = if (backIconResId != 0) backIconResId else android.R.drawable.ic_menu_revert,
            onBackClick = onBackClick,
            topBarHeight = topBarHeight,
            topBarTop = topBarTop,
            topBarLeft = topBarLeft,
            backButtonSize = backButtonSize,
            backIconSize = backIconSize,
            authorTextSize = authorTextSize,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = imageLeft, top = imageTop)
        ) {
            PhotoImageWithZoom(
                photo = photo,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                modifier = Modifier.width(imageWidth)
            )

            ImageActionsBar(
                downloadButtonWidth = downloadButtonWidth,
                downloadButtonHeight = downloadButtonHeight,
                downloadIconSize = downloadIconSize,
                downloadIconArrowSize = downloadIconArrowSize,
                downloadIconResId = if (downloadArrowResId != 0) downloadArrowResId else android.R.drawable.stat_sys_download,
                bookmarkButtonSize = bookmarkButtonSize,
                bookmarkIconSize = bookmarkIconSize,
                bookmarkIconResId = if (isBookmarked && bookmarkActiveResId != 0) {
                    bookmarkActiveResId
                } else if (!isBookmarked && bookmarkInactiveResId != 0) {
                    bookmarkInactiveResId
                } else {
                    if (isBookmarked) android.R.drawable.star_big_on else android.R.drawable.star_big_off
                },
                actionsBarWidth = actionsBarWidth,
                actionsBarHeight = actionsBarHeight,
                onDownloadClick = onDownloadClick,
                onBookmarkClick = onBookmarkClick,
                modifier = Modifier.width(actionsBarWidth)
            )
        }
    }
}

@Composable
private fun CustomTopBar(
    authorName: String,
    backIconResId: Int,
    onBackClick: () -> Unit,
    topBarHeight: androidx.compose.ui.unit.Dp,
    topBarTop: androidx.compose.ui.unit.Dp,
    topBarLeft: androidx.compose.ui.unit.Dp,
    backButtonSize: androidx.compose.ui.unit.Dp,
    backIconSize: androidx.compose.ui.unit.Dp,
    authorTextSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .offset(x = topBarLeft, y = topBarTop)
                .fillMaxWidth()
                .height(topBarHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(backButtonSize)
            ) {
                Image(
                    painter = painterResource(id = backIconResId),
                    contentDescription = "Back",
                    modifier = Modifier.size(backIconSize)
                )
            }

            if (authorName.isNotEmpty()) {
                Text(
                    text = authorName,
                    fontSize = authorTextSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PhotoImageWithZoom(
    photo: Photo,
    imageWidth: androidx.compose.ui.unit.Dp,
    imageHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isGestureActive by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (!isGestureActive && scale < 1f) 1f else scale,
        animationSpec = tween(300),
        label = "zoom_reset"
    )

    LaunchedEffect(isGestureActive) {
        if (!isGestureActive && scale < 1f) {
            scale = 1f
            offsetX = 0f
            offsetY = 0f
        }
    }

    Box(
        modifier = modifier
            .width(imageWidth)
            .height(imageHeight)
            // .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    isGestureActive = true
                    val newScale = (scale * zoom).coerceIn(1f, 3f)
                    scale = newScale
                    
                    if (scale <= 1f) {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            .pointerInput(scale) {
                kotlinx.coroutines.delay(500)
                if (!isGestureActive) {
                    if (scale < 1f) {
                        isGestureActive = false
                    }
                } else {
                    isGestureActive = false
                }
            }
    ) {
        SubcomposeAsyncImage(
            model = photo.largeImageUrl ?: photo.thumbnailUrl,
            contentDescription = photo.alt ?: "Photo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .graphicsLayer(
                    scaleX = animatedScale,
                    scaleY = animatedScale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = PrimaryRed
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error loading image", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun ImageActionsBar(
    downloadButtonWidth: androidx.compose.ui.unit.Dp,
    downloadButtonHeight: androidx.compose.ui.unit.Dp,
    downloadIconSize: androidx.compose.ui.unit.Dp,
    downloadIconArrowSize: androidx.compose.ui.unit.Dp,
    downloadIconResId: Int,
    bookmarkButtonSize: androidx.compose.ui.unit.Dp,
    bookmarkIconSize: androidx.compose.ui.unit.Dp,
    bookmarkIconResId: Int,
    actionsBarWidth: androidx.compose.ui.unit.Dp,
    actionsBarHeight: androidx.compose.ui.unit.Dp,
    onDownloadClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(actionsBarHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
            DownloadButton(
                width = downloadButtonWidth,
                height = downloadButtonHeight,
                iconSize = downloadIconSize,
                iconArrowSize = downloadIconArrowSize,
                iconResId = downloadIconResId,
                onClick = onDownloadClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(bookmarkButtonSize)
                    .background(
                        color = LightGray,
                        shape = RoundedCornerShape(bookmarkButtonSize / 2)
                    )
                    .clickable(onClick = onBookmarkClick),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = bookmarkIconResId),
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(bookmarkIconSize)
                )
            }
        }
    }

@Composable
private fun DownloadButton(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    iconArrowSize: androidx.compose.ui.unit.Dp,
    iconResId: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(
                color = LightGray,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(
                        color = PrimaryRed,
                        shape = RoundedCornerShape(iconSize / 2)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(iconArrowSize)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Download",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}
