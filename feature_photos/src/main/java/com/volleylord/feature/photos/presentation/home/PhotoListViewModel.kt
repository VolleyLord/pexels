package com.volleylord.feature.photos.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.volleylord.core.domain.models.Collection
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.usecases.photos.GetPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PhotoListViewModel @Inject constructor(
  private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

  private val defaultCollections = getDefaultCollections()

  private val _uiState = MutableStateFlow(
    PhotoListUiState(
      featuredCollections = defaultCollections.map { collection ->
        collection.copy(isSelected = false)
      },
      selectedCollection = null
    )
  )
  val uiState = _uiState.asStateFlow()

  @kotlinx.coroutines.ExperimentalCoroutinesApi
  val photos: Flow<PagingData<Photo>> = _uiState.flatMapLatest { state ->
    val query = when {
      state.searchQuery.isNotBlank() -> state.searchQuery
      state.selectedCollection != null -> state.selectedCollection.name.lowercase()
      else -> ""
    }
    getPhotosUseCase(query)
  }.cachedIn(viewModelScope)

  fun updateSearchQuery(query: String) {
    _uiState.update { state ->
      if (query.isNotBlank()) {
        val matchingCollection = state.featuredCollections.firstOrNull {
          it.name.lowercase() == query.trim().lowercase()
        }

        val updatedCollections = if (matchingCollection != null) {
          state.featuredCollections.map { col ->
            col.copy(isSelected = col.id == matchingCollection.id)
          }
        } else {
          state.featuredCollections.map { it.copy(isSelected = false) }
        }

        state.copy(
          searchQuery = query,
          featuredCollections = updatedCollections,
          selectedCollection = matchingCollection
        )
      } else {
        val updatedCollections = state.featuredCollections.map { col ->
          col.copy(isSelected = state.selectedCollection?.id == col.id)
        }
        state.copy(
          searchQuery = query,
          featuredCollections = updatedCollections
        )
      }
    }
  }

  fun selectCollection(collection: Collection) {
    _uiState.update { state ->
      if (state.selectedCollection?.id == collection.id) {
        return@update state
      }

      val updatedCollections = state.featuredCollections.map { col ->
        col.copy(isSelected = col.id == collection.id)
      }
      state.copy(
        featuredCollections = updatedCollections,
        selectedCollection = collection,
        searchQuery = collection.name
      )
    }
  }

  fun loadPopularPhotos() {
    _uiState.update { state ->
      val updatedCollections = state.featuredCollections.map { it.copy(isSelected = false) }
      state.copy(
        searchQuery = "",
        featuredCollections = updatedCollections,
        selectedCollection = null
      )
    }
  }

  private fun getDefaultCollections(): List<Collection> {
    return listOf(
      Collection(id = "ice", name = "Ice"),
      Collection(id = "watches", name = "Watches"),
      Collection(id = "drawing", name = "Drawing"),
      Collection(id = "nature", name = "Nature"),
      Collection(id = "architecture", name = "Architecture"),
      Collection(id = "technology", name = "Technology"),
      Collection(id = "animals", name = "Animals")
    )
  }
}


