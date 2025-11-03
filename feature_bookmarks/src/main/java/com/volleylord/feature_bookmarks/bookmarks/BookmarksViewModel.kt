package com.volleylord.feature_bookmarks.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.usecases.photos.GetBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel for the Bookmarks screen.
 * Manages the state and data flow for displaying bookmarked photos.
 */
@HiltViewModel
class BookmarksViewModel @Inject constructor(
  private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

  /**
   * Flow of paginated bookmarked photos.
   */
  val bookmarks: Flow<PagingData<Photo>> = getBookmarksUseCase()
    .cachedIn(viewModelScope)
}
