package com.volleylord.core.core.network

/**
 * Sealed class representing the result of a network operation.
 *
 * @param T The type of data returned in a successful operation.
 */
sealed class NetworkResult<out T> {

  /**
   * Indicates the network operation was successful.
   *
   * @param data The data returned from the operation.
   */
  data class Success<T>(val data: T) : NetworkResult<T>()

  /**
   * Indicates the network operation failed.
   *
   * @param message The error message describing the failure.
   */
  data class Error(val message: String) : NetworkResult<Nothing>()

  /**
   * Indicates the network operation is in progress.
   */
  data object Loading : NetworkResult<Nothing>()
}

/**
 * Executes the provided action if the network result is successful.
 *
 * @param action The action to perform with the successful data.
 * @return The original [NetworkResult] for chaining.
 */
inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
  if (this is NetworkResult.Success) action(data)
  return this
}

/**
 * Executes the provided action if the network result is an error.
 *
 * @param action The action to perform with the error message.
 * @return The original [NetworkResult] for chaining.
 */
inline fun <T> NetworkResult<T>.onError(action: (String) -> Unit): NetworkResult<T> {
  if (this is NetworkResult.Error) action(message)
  return this
}