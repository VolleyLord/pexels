package com.volleylord.core.core.network

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException as JavaSocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility object for detecting and handling network-related errors.
 */
object NetworkErrorUtils {

  /**
   * Checks if the given exception is a network-related error.
   *
   * @param exception The exception to check.
   * @return true if the exception is network-related, false otherwise.
   */
  fun isNetworkError(exception: Throwable): Boolean {
    return when (exception) {
      is IOException,
      is UnknownHostException,
      is ConnectException,
      is JavaSocketTimeoutException,
      is ConnectTimeoutException,
      is SocketTimeoutException,
      is HttpRequestTimeoutException -> true
      is ServerResponseException -> {
        // Consider 5xx errors as network/server issues
        exception.response.status.value in 500..599
      }
      else -> {
        // Check cause chain for network errors
        var cause = exception.cause
        while (cause != null) {
          if (cause is IOException ||
              cause is UnknownHostException ||
              cause is ConnectException ||
              cause is JavaSocketTimeoutException ||
              cause is ConnectTimeoutException ||
              cause is SocketTimeoutException ||
              cause is HttpRequestTimeoutException
          ) {
            return true
          }
          cause = cause.cause
        }
        false
      }
    }
  }

  /**
   * Gets a user-friendly error message for the given exception.
   *
   * @param exception The exception to get a message for.
   * @return A user-friendly error message.
   */
  fun getUserFriendlyMessage(exception: Throwable): String {
    return when {
      isNetworkError(exception) -> {
        when (exception) {
          is UnknownHostException,
          is ConnectException -> "No internet connection"
          is JavaSocketTimeoutException,
          is SocketTimeoutException,
          is HttpRequestTimeoutException -> "Connection timeout. Please try again"
          else -> "Network error. Please check your connection and try again"
        }
      }
      exception.message != null -> exception.message!!
      else -> "An error occurred"
    }
  }
}

