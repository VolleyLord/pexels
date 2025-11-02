package com.volleylord.core.data.remote.api

import com.volleylord.core.data.remote.dto.PhotoDto
import com.volleylord.core.data.remote.dto.PhotosResponseDto

/**
 * Interface for making photo-related API calls to the Pexels API.
 */
interface PhotosApi {

  /**
   * Fetches a paginated list of curated photos.
   *
   * @param apiKey The API key for authentication.
   * @param page The page number to fetch, or null for the first page.
   * @param perPage The number of photos per page, or null for default.
   * @return A [PhotosResponseDto] containing the list of photos and pagination metadata.
   * @throws Exception If the API call fails (e.g., network error, invalid API key).
   */
  suspend fun getPhotos(apiKey: String, page: Int?, perPage: Int?): PhotosResponseDto

  /**
   * Searches for photos by query (category/keyword).
   *
   * @param apiKey The API key for authentication.
   * @param query The search query (e.g., category name like "ice", "watches").
   * @param page The page number to fetch, or null for the first page.
   * @param perPage The number of photos per page, or null for default.
   * @return A [PhotosResponseDto] containing the list of photos and pagination metadata.
   * @throws Exception If the API call fails (e.g., network error, invalid API key).
   */
  suspend fun searchPhotos(apiKey: String, query: String, page: Int?, perPage: Int?): PhotosResponseDto

  /**
   * Fetches details of a specific photo by its ID.
   *
   * @param apiKey The API key for authentication.
   * @param id The ID of the photo to retrieve.
   * @return A [PhotoDto] containing the photo details.
   * @throws Exception If the API call fails (e.g., network error, invalid API key).
   */
  suspend fun getPhoto(apiKey: String, id: Int): PhotoDto
}