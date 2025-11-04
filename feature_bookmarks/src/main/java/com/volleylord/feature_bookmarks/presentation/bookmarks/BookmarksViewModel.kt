package com.volleylord.feature_bookmarks.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.usecases.photos.GetBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
  private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

  val bookmarks: Flow<PagingData<Photo>> = getBookmarksUseCase()
    .cachedIn(viewModelScope)
}


