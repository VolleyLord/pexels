package com.volleylord.feature.photos.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.theme.White
import com.volleylord.core.ui.components.AppLinearProgressIndicator
import com.volleylord.core.ui.components.ImageNotFoundStub
import com.volleylord.feature.photos.presentation.details.components.DetailsPhotoZoom
import com.volleylord.feature.photos.presentation.details.components.BottomActions
import com.volleylord.feature.photos.presentation.details.components.DetailTopBar


@Composable
fun PhotoDetailScreen(
    photoId: Int,
    viewModel: PhotoDetailViewModel,
    onBackClick: () -> Unit,
    isFromBookmarks: Boolean = false,
    onExploreClick: () -> Unit = {}
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
            onExploreClick = onExploreClick,
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
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is PhotoDetailUiState.Loading -> {
            PhotoDetailLoading(
                onBackClick = onBackClick,
                modifier = modifier.fillMaxSize()
            )
        }

        is PhotoDetailUiState.Error -> {
            ImageNotFoundStub(onBackClick = onBackClick, onExploreClick = onExploreClick, modifier = modifier)
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
            } ?: ImageNotFoundStub(onBackClick = onBackClick, onExploreClick = onExploreClick, modifier = modifier)
        }
    }
}

@Composable
private fun PhotoDetailLoading(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val horizontalPadding = 24.dp
    val topBarTop = 61.dp
    val topBarHeight = 40.dp
    val backBtnSize = 40.dp
    val titleInset = 57.dp
    val titleFontSize = 18.sp
    val imageTopSpacer = 29.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(topBarTop))
        DetailTopBar(
            title = "",
            onBackClick = onBackClick,
            topBarHeight = topBarHeight.value.toInt(),
            backBtnSize = backBtnSize.value.toInt(),
            titleInset = titleInset.value.toInt(),
            titleFontSize = titleFontSize.value.toInt()
        )

        Spacer(modifier = Modifier.height(imageTopSpacer))

        AppLinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
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
        // no scaling , Figma sizes
    val horizontalPadding = 24.dp
    val topBarTop = 61.dp
    val topBarHeight = 40.dp
    val backBtnSize = 40.dp
    val titleInset = 57.dp
    val titleFontSize = 18.sp
    val imageTopSpacer = 29.dp
    val imageHeight = 586.dp
    val actionsTopSpacer = 24.dp
    val actionsHeight = 48.dp
    val downloadWidth = 180.dp
    val bookmarkSize = 48.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(topBarTop))
        DetailTopBar(
            title = photo.photographer ?: "",
            onBackClick = onBackClick,
            topBarHeight = topBarHeight.value.toInt(),
            backBtnSize = backBtnSize.value.toInt(),
            titleInset = titleInset.value.toInt(),
            titleFontSize = titleFontSize.value.toInt()
        )

        Spacer(modifier = Modifier.height(imageTopSpacer))

        DetailsPhotoZoom(
            imageUrl = photo.originalImageUrl ?: photo.large2xImageUrl ?: photo.largeImageUrl ?: photo.thumbnailUrl,
            modifier = Modifier,
            fixedHeightDp = imageHeight.value.toInt(),
            cornerRadiusDp = 24,
            intrinsicWidth = photo.width,
            intrinsicHeight = photo.height
        )

        Spacer(modifier = Modifier.height(actionsTopSpacer))

        BottomActions(
            isBookmarked = isBookmarked,
            onDownloadClick = onDownloadClick,
            onBookmarkClick = onBookmarkClick,
            actionsHeightDp = actionsHeight.value.toInt(),
            downloadWidthDp = downloadWidth.value.toInt()
        )
    }
}




