package com.volleylord.pexels.navigation

import kotlinx.serialization.Serializable

/**
 * Sealed interface representing all possible navigation destinations in the application.
 * This provides compile-time safety for navigation routes.
 */
sealed interface AppDestination

/**
 * Navigation destination for the photo list screen (Home Screen).
 */
@Serializable
data object PhotoListDestination: AppDestination

/**
 * Navigation destination for the photo detail screen.
 *
 * @property photoId The ID of the selected photo to display.
 * @property isFromBookmarks Whether the photo is being loaded from bookmarks.
 */
@Serializable
data class PhotoDetailDestination(
    val photoId: Int,
    val isFromBookmarks: Boolean = false
): AppDestination

/**
 * Navigation destination for the bookmarks screen.
 */
@Serializable
data object BookmarksDestination: AppDestination