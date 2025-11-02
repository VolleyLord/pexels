package com.volleylord.core.core.network

import android.util.Log
import com.volleylord.core.BuildConfig
import com.volleylord.core.core.network.NetworkConfig.BASE_URL
import com.volleylord.core.core.network.NetworkConfig.CONNECT_TIMEOUT_MILLIS
import com.volleylord.core.core.network.NetworkConfig.SOCKET_TIMEOUT_MILLIS
import com.volleylord.core.core.network.NetworkConfig.TIMEOUT_MILLIS
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Contains network configuration constants for the Ktor HTTP client.
 */
object NetworkConfig {
  /** Base URL for all API requests. */
  const val BASE_URL = "https://api.pexels.com/"

  /** Total request timeout in milliseconds. */
  const val TIMEOUT_MILLIS = 30_000L

  /** Connection timeout in milliseconds. */
  const val CONNECT_TIMEOUT_MILLIS = 30_000L

  /** Socket read timeout in milliseconds. */
  const val SOCKET_TIMEOUT_MILLIS = 30_000L
}

/**
 * Factory class for creating a configured instance of [HttpClient] using the Android engine.
 *
 * This class is responsible for setting up:
 * - Content negotiation with [Json] serialization
 * - Logging (enabled in debug mode)
 * - Response observation for logging HTTP status codes
 * - Timeout settings
 * - Default headers and base URL
 *
 * @constructor Creates an instance of [HttpClientFactory]. Marked with [Inject] to allow usage with Dagger/Hilt.
 */
class HttpClientFactory @Inject constructor() {

  /**
   * Creates and configures a new [HttpClient] instance with the provided [Json] configuration.
   *
   * @param json The [Json] instance used for serialization/deserialization.
   * @return A fully configured [HttpClient] instance.
   */
  fun create(json: Json): HttpClient {
    return HttpClient(Android) {

      // Content negotiation plugin for JSON serialization
      install(ContentNegotiation) {
        json(json)
      }

      // Logging plugin for debugging HTTP requests and responses
      install(Logging) {
        logger = object : Logger {
          override fun log(message: String) {
            Log.d("Ktor", message)
          }
        }
        level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
      }

      // Observes and logs the HTTP response status codes
      install(ResponseObserver) {
        onResponse { response ->
          Log.d("Ktor", "Response: ${response.status.value} ${response.status.description}")
        }
      }

      // Timeout configuration for requests
      install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT_MILLIS
        connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
        socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
      }

      // Sets default request parameters such as base URL and headers
      defaultRequest {
        url(BASE_URL)
        header("Accept", "application/json")
      }
    }
  }
}