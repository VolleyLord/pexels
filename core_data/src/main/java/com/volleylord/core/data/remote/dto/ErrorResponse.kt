package com.volleylord.core.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data class representing the JSON structure of an error response from the Pexels API.
 */
@Serializable
data class ErrorResponse(
  /** The error message, if available. */
  val error: String? = null,
  /** The error code, if available. */
  val code: String? = null,
  /** The HTTP status code, if available. */
  val status: Int? = null
)