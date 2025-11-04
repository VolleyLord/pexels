package com.volleylord.pexels.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.volleylord.feature.photos.presentation.details.PhotoDetailScreen
import com.volleylord.feature.photos.presentation.home.PhotoListScreen
import com.volleylord.feature_bookmarks.presentation.bookmarks.BookmarksScreen

/**
 * Sets up the main navigation graph of the app using Jetpack Navigation and Hilt.
 *
 * @param startDestination The initial destination to launch (e.g., [PhotoListDestination]).
 */
@Composable
fun AppNavigation(startDestination: AppDestination) {
  val navController = rememberNavController()

  NavHost(
    navController = navController,
    startDestination = startDestination
  ) {
    composable<PhotoListDestination> {
      PhotoListScreen(
        viewModel = hiltViewModel(),
        onPhotoClick = { photoId ->
          navController.navigate(PhotoDetailDestination(photoId))
        },
        onNavigateToBookmarks = {
          navController.navigate(BookmarksDestination) {
            popUpTo(PhotoListDestination) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        },
        selectedTab = 0
      )
    }

    composable<BookmarksDestination> {
      BookmarksScreen(
        viewModel = hiltViewModel(),
        onPhotoClick = { photoId ->
          navController.navigate(PhotoDetailDestination(photoId, isFromBookmarks = true))
        },
        onExploreClick = {
          navController.navigate(PhotoListDestination) {
            popUpTo(BookmarksDestination) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        },
        onNavigateToHome = {
          navController.navigate(PhotoListDestination) {
            popUpTo(BookmarksDestination) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        }
      )
    }

    composable<PhotoDetailDestination> { backStackEntry ->
      val args = backStackEntry.toRoute<PhotoDetailDestination>()
      PhotoDetailScreen(
        photoId = args.photoId,
        viewModel = hiltViewModel(),
        onBackClick = {
          navController.popBackStack()
        },
        isFromBookmarks = args.isFromBookmarks
      )
    }
  }
}