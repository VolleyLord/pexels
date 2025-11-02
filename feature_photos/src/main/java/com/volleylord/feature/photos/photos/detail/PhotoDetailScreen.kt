package com.volleylord.feature.photos.photos.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ImageAspectRatio
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.volleylord.common.R
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.ui.image.AsyncImageWithPlaceholder

/**
 * Composable function that renders the photo detail screen.
 *
 * @param photoId The ID of the photo to display.
 * @param viewModel The ViewModel for managing photo detail data.
 * @param onBackClick Callback invoked when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
  photoId: Int,
  viewModel: PhotoDetailViewModel,
  onBackClick: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val uriHandler = LocalUriHandler.current

  LaunchedEffect(photoId) {
    viewModel.loadPhoto(photoId)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.photo_details_screen_title),
            style = MaterialTheme.typography.headlineSmall
          )
        },
        navigationIcon = {
          val backDescription = stringResource(R.string.content_description_navigate_back)
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.semantics {
              contentDescription = backDescription
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = null
            )
          }
        }
      )
    }
  ) { paddingValues ->
    PhotoDetailContent(
      uiState = uiState,
      uriHandler = uriHandler,
      onRetry = { viewModel.loadPhoto(photoId) },
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    )
  }
}

/**
 * Renders the content of the photo detail screen based on the UI state.
 *
 * @param uiState The current UI state of the photo detail screen.
 * @param uriHandler The handler for opening URLs in a browser.
 * @param onRetry Callback invoked to retry loading the photo.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun PhotoDetailContent(
  uiState: PhotoDetailUiState,
  uriHandler: UriHandler,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
    when (uiState) {
      is PhotoDetailUiState.Loading -> {
        LoadingState(
          modifier = Modifier.align(Alignment.Center)
        )
      }

      is PhotoDetailUiState.Error -> {
        ErrorState(
          message = uiState.message,
          onRetry = onRetry,
          modifier = Modifier.align(Alignment.Center)
        )
      }

      is PhotoDetailUiState.Success -> {
        PhotoDetailSuccess(
          photo = uiState.photo,
          uriHandler = uriHandler,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

/**
 * Renders the loading state for the photo detail screen.
 *
 * @param modifier The modifier for the composable.
 */
@Composable
private fun LoadingState(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    CircularProgressIndicator(
      modifier = Modifier.size(48.dp),
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = stringResource(R.string.photo_details_loading_message),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
  }
}

/**
 * Renders the error state for the photo detail screen.
 *
 * @param message The error message to display, if any.
 * @param onRetry Callback invoked to retry loading the photo.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun ErrorState(
  message: String?,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Icon(
      imageVector = Icons.Default.Info,
      contentDescription = null,
      modifier = Modifier.size(48.dp),
      tint = MaterialTheme.colorScheme.error
    )

    Text(
      text = stringResource(R.string.error_photo_details_load_failed),
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.error
    )

    Text(
      text = message ?: stringResource(R.string.error_unknown),
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
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
 * Renders the success state for the photo detail screen.
 *
 * @param photo The photo to display, if available.
 * @param uriHandler The handler for opening URLs in a browser.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun PhotoDetailSuccess(
  photo: Photo?,
  uriHandler: UriHandler,
  modifier: Modifier = Modifier
) {
  photo ?: return

  val aspectRatio = calculateAspectRatio(photo.width, photo.height)

  Column(
    modifier = modifier.verticalScroll(rememberScrollState())
  ) {
    // Main photo section
    PhotoSection(
      photo = photo,
      aspectRatio = aspectRatio
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Details section
    PhotoDetailsSection(
      photo = photo,
      uriHandler = uriHandler,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    // Bottom padding for better scrolling experience
    Spacer(modifier = Modifier.height(32.dp))
  }
}

/**
 * Renders the photo section of the detail screen.
 *
 * @param photo The photo to display.
 * @param aspectRatio The aspect ratio of the photo.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun PhotoSection(
  photo: Photo,
  aspectRatio: Float,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(8.dp)
  ) {
    Box(
      modifier = Modifier
        .background(photo.avgColor ?: Color.White)
        .clip(RoundedCornerShape(8.dp))
    ) {
      AsyncImageWithPlaceholder(
        imageUrl = photo.largeImageUrl,
        placeholder = rememberAsyncImagePainter(
          model = photo.tinyThumbnailUrl,
          contentScale = ContentScale.Crop
        ),
        contentDescription = photo.alt
          ?: stringResource(R.string.content_description_photo_details_image),
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(aspectRatio)
      )
    }
  }
}

/**
 * Renders the photo section of the detail screen.
 *
 * @param photo The photo to display.
 * @param aspectRatio The aspect ratio of the photo.
 * @param modifier The modifier for the composable.
 */
@Composable
private fun PhotoDetailsSection(
  photo: Photo,
  uriHandler: UriHandler,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(
      text = stringResource(R.string.photo_details_section_title),
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(bottom = 16.dp)
    )

    // Photographer info
    photo.photographer?.takeIf { it.isNotBlank() }?.let { photographer ->
      DetailCard(
        icon = Icons.Rounded.PhotoCamera,
        title = stringResource(R.string.photo_details_label_photographer),
        value = photographer,
        modifier = Modifier.padding(bottom = 12.dp)
      )
    }

    // Description
    photo.alt?.takeIf { it.isNotBlank() }?.let { description ->
      DetailCard(
        icon = Icons.Rounded.Description,
        title = stringResource(R.string.photo_details_label_description),
        value = description,
        modifier = Modifier.padding(bottom = 12.dp)
      )
    }

    // Dimensions
    if (photo.width != null && photo.height != null && photo.width!! > 0 && photo.height!! > 0) {
      DetailCard(
        icon = Icons.Rounded.ImageAspectRatio,
        title = stringResource(R.string.photo_details_label_dimensions),
        value = stringResource(
          R.string.photo_details_dimensions_format,
          photo.width!!,
          photo.height!!
        ),
        modifier = Modifier.padding(bottom = 12.dp)
      )
    }

    // View on web button
    photo.url?.let { url ->
      Spacer(modifier = Modifier.height(8.dp))

      val description = stringResource(R.string.content_description_open_in_browser)
      OutlinedButton(
        onClick = { uriHandler.openUri(url) },
        modifier = Modifier
          .fillMaxWidth()
          .semantics {
            role = Role.Button
            contentDescription = description
          }
      ) {
        Text(
          text = stringResource(R.string.photo_details_action_view_on_web),
          style = MaterialTheme.typography.labelLarge
        )
      }
    }
  }
}

/**
 * Renders a card displaying a single detail (e.g., photographer or description).
 *
 * @param title The title of the detail.
 * @param value The value of the detail.
 * @param modifier The modifier for the composable.
 * @param icon An optional icon to display next to the detail.
 */
@Composable
private fun DetailCard(
  title: String,
  value: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
    shape = RoundedCornerShape(8.dp)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      icon?.let {
        Icon(
          imageVector = it,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = value,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          overflow = TextOverflow.Ellipsis
        )
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
    1f
  }
}