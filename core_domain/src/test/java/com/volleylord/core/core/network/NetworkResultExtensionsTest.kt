package com.volleylord.core.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [NetworkResult] extension functions: [onSuccess] and [onError].
 */
class NetworkResultExtensionsTest {

  // Dummy data for testing
  private val testData = "Success Data"
  private val testError = "Error Message"

  /**
   * Verifies that [onSuccess] executes the action for [NetworkResult.Success].
   */
  @Test
  fun `onSuccess executes action for Success result`() {
    // Given a Success result
    val result = NetworkResult.Success(testData)
    var successActionExecuted = false
    var receivedData: String? = null

    // When onSuccess is called
    val returnedResult = result.onSuccess { data ->
      successActionExecuted = true
      receivedData = data
    }

    // Then the action should be executed, and the data should be correct
    assertTrue(successActionExecuted)
    assertEquals(testData, receivedData)
    // And the original result should be returned for chaining
    assertEquals(result, returnedResult)
  }

  /**
   * Verifies that [onSuccess] does not execute the action for [NetworkResult.Error].
   */
  @Test
  fun `onSuccess does not execute action for Error result`() {
    // Given an Error result
    val result: NetworkResult<String> = NetworkResult.Error(testError)
    var successActionExecuted = false

    // When onSuccess is called
    val returnedResult = result.onSuccess {
      successActionExecuted = true
    }

    // Then the action should not be executed
    assertFalse(successActionExecuted)
    // And the original result should be returned for chaining
    assertEquals(result, returnedResult)
  }

  /**
   * Verifies that [onSuccess] does not execute the action for [NetworkResult.Loading].
   */
  @Test
  fun `onSuccess does not execute action for Loading result`() {
    // Given a Loading result
    val result: NetworkResult<String> = NetworkResult.Loading
    var successActionExecuted = false

    // When onSuccess is called
    val returnedResult = result.onSuccess {
      successActionExecuted = true
    }

    // Then the action should not be executed
    assertFalse(successActionExecuted)
    // And the original result should be returned for chaining
    assertEquals(result, returnedResult)
  }

  /**
   * Verifies that [onError] executes the action for [NetworkResult.Error].
   */
  @Test
  fun `onError executes action for Error result`() {
    // Given an Error result
    val result: NetworkResult<String> = NetworkResult.Error(testError)
    var errorActionExecuted = false
    var receivedMessage: String? = null

    // When onError is called
    val returnedResult = result.onError { message ->
      errorActionExecuted = true
      receivedMessage = message
    }

    // Then the action should be executed, and the message should be correct
    assertTrue(errorActionExecuted)
    assertEquals(testError, receivedMessage)
    // And the original result should be returned for chaining
    assertEquals(result, returnedResult)
  }

  /**
   * Verifies that [onError] does not execute the action for [NetworkResult.Success].
   */
  @Test
  fun `onError does not execute action for Success result`() {
    // Given a Success result
    val result = NetworkResult.Success(testData)
    var errorActionExecuted = false

    // When onError is called
    val returnedResult = result.onError {
      errorActionExecuted = true
    }

    // Then the action should not be executed
    assertFalse(errorActionExecuted)
    // And the original result should be returned for chaining
    assertEquals(result, returnedResult)
  }

  /**
   * Verifies that [onError] does not execute the action for [NetworkResult.Loading].
   */
  @Test
  fun `onError does not execute action for Loading result`() {
    // Given a Loading result
    val result: NetworkResult<String> = NetworkResult.Loading
    var errorActionExecuted = false

    // When onError is called
    val returnedResult = result.onError {
      errorActionExecuted = true
    }

    // Then the action should not be executed
    assertFalse(errorActionExecuted)
    // And the original result should be returned for chaining
    assertEquals(result, returnedResult)
  }
}