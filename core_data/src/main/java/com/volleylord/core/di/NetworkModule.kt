package com.volleylord.core.di

import com.volleylord.core.core.network.HttpClientFactory
import com.volleylord.core.data.remote.api.PhotosApi
import com.volleylord.core.data.remote.service.PhotosService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides networking-related dependencies such as [HttpClient],
 * JSON configuration, and API services.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  /**
   * Provides the [Json] instance configured for serialization and deserialization.
   *
   * - `ignoreUnknownKeys = true` allows unknown fields in JSON to be ignored.
   * - `isLenient = true` allows relaxed JSON parsing.
   * - `encodeDefaults = true` includes default values in output.
   *
   * @return A configured [Json] instance.
   */
  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
  }

  /**
   * Provides the [HttpClient] for making HTTP requests with Ktor.
   *
   * @param json The configured [Json] instance for serialization.
   * @return The [HttpClient] instance.
   */
  @Provides
  @Singleton
  fun provideHttpClient(json: Json): HttpClient {
    return HttpClientFactory().create(json)
  }

  /**
   * Provides the [PhotosApi] implementation using [HttpClient].
   *
   * @param httpClient The HTTP client used to make network requests.
   * @return An instance of [PhotosApi].
   */
  @Provides
  @Singleton
  fun providePhotosApi(httpClient: HttpClient): PhotosApi {
    return PhotosService(httpClient)
  }
}