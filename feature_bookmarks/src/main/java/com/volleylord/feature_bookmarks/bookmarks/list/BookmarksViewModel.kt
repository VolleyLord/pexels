package com.volleylord.feature_bookmarks.bookmarks.list

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
 * ViewModel for managing bookmarked photos.
 *
 * @param getBookmarksUseCase Use case for fetching paginated bookmarked photos.
 */
@HiltViewModel
class BookmarksViewModel @Inject constructor(
  private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

  /**
   * Flow of paginated bookmarked photos.
   */
  val photos: Flow<PagingData<Photo>> = getBookmarksUseCase()
    .cachedIn(viewModelScope)
}

