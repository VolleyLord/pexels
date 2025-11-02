package com.volleylord.core.data.remote.service

import com.volleylord.core.data.remote.api.PhotosApi
import com.volleylord.core.data.remote.dto.ErrorResponse
import com.volleylord.core.data.remote.dto.PhotoDto
import com.volleylord.core.data.remote.dto.PhotosResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import javax.inject.Inject

/**
 * Implementation of [PhotosApi] using Ktor for making HTTP requests to the Pexels API.
 */
class PhotosService @Inject constructor(private val httpClient: HttpClient) : PhotosApi {

  /**
   * Fetches a paginated list of curated photos from the Pexels API.
   *
   * @param apiKey The API key for authentication.
   * @param page The page number to fetch, or null for the first page.
   * @param perPage The number of photos per page, or null for default.
   * @return A [PhotosResponseDto] containing the list of photos and pagination metadata.
   * @throws Exception If the API call fails (e.g., network error, invalid API key).
   */
  override suspend fun getPhotos(apiKey: String, page: Int?, perPage: Int?): PhotosResponseDto {
    val response: HttpResponse = httpClient.get("v1/curated") {
      headers {
        append(HttpHeaders.Authorization, apiKey)
      }
      parameter("page", page)
      parameter("per_page", perPage)
    }

    if (response.status.isSuccess()) {
      return response.body()
    } else {
      throw parseError(response)
    }
  }

  /**
   * Searches for photos by query (category/keyword) from the Pexels API.
   *
   * @param apiKey The API key for authentication.
   * @param query The search query (e.g., category name like "ice", "watches").
   * @param page The page number to fetch, or null for the first page.
   * @param perPage The number of photos per page, or null for default.
   * @return A [PhotosResponseDto] containing the list of photos and pagination metadata.
   * @throws Exception If the API call fails (e.g., network error, invalid API key).
   */
  override suspend fun searchPhotos(apiKey: String, query: String, page: Int?, perPage: Int?): PhotosResponseDto {
    val response: HttpResponse = httpClient.get("v1/search") {
      headers {
        append(HttpHeaders.Authorization, apiKey)
      }
      parameter("query", query)
      parameter("page", page)
      parameter("per_page", perPage)
    }

    if (response.status.isSuccess()) {
      return response.body()
    } else {
      throw parseError(response)
    }
  }

  /**
   * Fetches details of a specific photo by its ID from the Pexels API.
   *
   * @param apiKey The API key for authentication.
   * @param id The ID of the photo to retrieve.
   * @return A [PhotoDto] containing the photo details.
   * @throws Exception If the API call fails (e.g., network error, invalid API key).
   */
  override suspend fun getPhoto(apiKey: String, id: Int): PhotoDto {
    val response: HttpResponse = httpClient.get("v1/photos/$id") {
      headers {
        append(HttpHeaders.Authorization, apiKey)
      }
    }

    if (response.status.isSuccess()) {
      return response.body()
    } else {
      throw parseError(response)
    }
  }

  /**
   * Parses the body of a failed API response into an [ErrorResponse].
   *
   * @param response The failed [HttpResponse] to parse.
   * @return An [Exception] with a detailed error message.
   */
  private suspend fun parseError(response: HttpResponse): Exception {
    return try {
      val errorResponse = response.body<ErrorResponse>()
      // Use the 'error' or 'code' field from the JSON for a better message.
      val errorMessage = errorResponse.error ?: errorResponse.code ?: "Unknown API Error"
      Exception("API Error: $errorMessage (Status: ${response.status.value})")
    } catch (e: Exception) {
      // This is a fallback in case the error body is not the expected JSON.
      Exception("API Error: Failed to parse error response. (Status: ${response.status.value})")
    }
  }
} 