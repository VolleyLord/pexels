package com.volleylord.feature.photos.photos.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.volleylord.core.domain.models.Collection
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.usecases.photos.GetPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the photo list data.
 *
 * @param getPhotosUseCase Use case for fetching paginated photos.
 */
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
      selectedCollection = null // Curated shown when no collection is chosen
    )
  )
  val uiState = _uiState.asStateFlow()

  /**
   * Flow of paginated photos based on selected collection.
   *  flatMapLatest дto switch between requests.
   */
  @kotlinx.coroutines.ExperimentalCoroutinesApi
  val photos: Flow<PagingData<Photo>> = _uiState.flatMapLatest { state ->
    val query = when {
      state.searchQuery.isNotBlank() -> state.searchQuery
      state.selectedCollection != null -> state.selectedCollection.name.lowercase()
      else -> "" // Curated
    }
    getPhotosUseCase(query)
  }.cachedIn(viewModelScope)

  init {
    loadPhotosForSelectedCollection()
  }

  /**
   * Updates the search query (for manual search)
   */
  fun updateSearchQuery(query: String) {
    _uiState.update { state ->
      if (query.isNotBlank()) {
        // Check if query matches any collection name (case-insensitive)
        val matchingCollection = state.featuredCollections.firstOrNull { 
          it.name.lowercase() == query.trim().lowercase() 
        }
        
        val updatedCollections = if (matchingCollection != null) {
          // If query matches a collection, mark it as selected
          state.featuredCollections.map { col ->
            col.copy(isSelected = col.id == matchingCollection.id)
          }
        } else {
          // No match, deselect all
          state.featuredCollections.map { it.copy(isSelected = false) }
        }
        
        state.copy(
          searchQuery = query,
          featuredCollections = updatedCollections,
          selectedCollection = matchingCollection
        )
      } else {
        // If search is empty, return to selected category or Curated (null = Curated)
        val updatedCollections = state.featuredCollections.map { col ->
          col.copy(isSelected = state.selectedCollection?.id == col.id)
        }
        state.copy(
          searchQuery = query,
          featuredCollections = updatedCollections
        )
      }
    }
    loadPhotosForSelectedCollection()
  }

  /**
   * Handles collection selection.
   * Sets the collection name in search bar and triggers search automatically.
   */
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
        searchQuery = collection.name // Set collection name in search bar
      )
    }
    loadPhotosForSelectedCollection()
  }

  /**
   * Loads images for selected category.
   */
  private fun loadPhotosForSelectedCollection() {
  }

  /**
   * Loads popular curated photos (clears search and resets collection selection).
   */
  fun loadPopularPhotos() {
    _uiState.update { state ->
      val updatedCollections = state.featuredCollections.map { it.copy(isSelected = false) }
      state.copy(
        searchQuery = "",
        featuredCollections = updatedCollections,
        selectedCollection = null // Curated
      )
    }
    loadPhotosForSelectedCollection()
  }

  /**
   * Returns the default 7 popular collections (без Curated, так как она показывается по умолчанию).
   */
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