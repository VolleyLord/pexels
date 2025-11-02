package com.volleylord.core.data.local.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.volleylord.core.data.security.CryptoManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurePreferencesDataSourceTest {

  private lateinit var dataSource: SecurePreferencesDataSource
  private lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    dataSource = SecurePreferencesDataSource(context, CryptoManager())
  }

  @After
  fun tearDown() = runBlocking {
    dataSource.clearApiKey()
    dataSource.setInitialSeedComplete() // Reset for other tests
  }

  @Test
  fun saveAndGetApiKey_returnsSameKey() = runBlocking {
    val apiKey = "my-secret-api-key-12345"
    dataSource.saveApiKey(apiKey)
    val retrievedKey = dataSource.getApiKey().first()
    assertEquals(apiKey, retrievedKey)
  }

  @Test
  fun getApiKey_returnsNullWhenNoKeyIsSaved() = runBlocking {
    val retrievedKey = dataSource.getApiKey().first()
    assertNull(retrievedKey)
  }

  @Test
  fun clearApiKey_removesTheKey() = runBlocking {
    val apiKey = "my-secret-api-key-to-be-cleared"
    dataSource.saveApiKey(apiKey)
    dataSource.clearApiKey()
    val retrievedKey = dataSource.getApiKey().first()
    assertNull(retrievedKey)
  }

  @Test
  fun initialSeedComplete_isFalseByDefault_andCanBeSetToTrue() = runBlocking {
    // Ensure it is false initially
    val initialStatus = dataSource.isInitialSeedComplete().first()
    assertFalse(initialStatus)

    // Set it to true
    dataSource.setInitialSeedComplete()
    val finalStatus = dataSource.isInitialSeedComplete().first()
    assertTrue(finalStatus)
  }
}