package com.volleylord.core.data.security

import io.mockk.every
import io.mockk.mockk
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.security.KeyStore
import java.security.Security
import javax.crypto.spec.SecretKeySpec

/**
 * Unit tests for [CryptoManager].
 *
 * Tests verify correct encryption and decryption behavior using a mocked [KeyStore].
 * BouncyCastle provider is registered to support AES/CBC/PKCS7Padding transformation.
 */
class CryptoManagerTest {

  private lateinit var cryptoManager: CryptoManager
  private val testKeyAlias = "test_key_alias"
  private val mockKeyStore = mockk<KeyStore>(relaxed = true)

  /**
   * Sets up the test environment by registering the BouncyCastle provider
   * and mocking the [KeyStore] to return a dummy AES secret key for testing.
   */
  @Before
  fun setup() {
    Security.addProvider(BouncyCastleProvider())
    cryptoManager = CryptoManager(mockKeyStore)

    val dummySecretKey = SecretKeySpec(ByteArray(16) { 0x01 }, "AES")

    every {
      mockKeyStore.getEntry(testKeyAlias, null)
    } returns KeyStore.SecretKeyEntry(dummySecretKey)
  }

  /**
   * Verifies that encrypting and then decrypting a string returns the original string.
   */
  @Test
  fun `encrypt and decrypt returns original text`() {
    val originalText = "test-api-key"
    val encrypted = cryptoManager.encrypt(originalText, testKeyAlias)
    val decrypted = cryptoManager.decrypt(encrypted, testKeyAlias)
    assertEquals(originalText, decrypted)
  }

  /**
   * Ensures that encrypting the same input multiple times produces different ciphertexts,
   * due to unique initialization vectors, and that both decrypt correctly.
   */
  @Test
  fun `encrypt produces different ciphertext for same data`() {
    val originalText = "test-api-key"

    val encrypted1 = cryptoManager.encrypt(originalText, testKeyAlias)
    val encrypted2 = cryptoManager.encrypt(originalText, testKeyAlias)

    assertFalse(encrypted1.data.contentEquals(encrypted2.data))
    assertFalse(encrypted1.iv.contentEquals(encrypted2.iv))
    assertEquals(originalText, cryptoManager.decrypt(encrypted1, testKeyAlias))
    assertEquals(originalText, cryptoManager.decrypt(encrypted2, testKeyAlias))
  }

  /**
   * Checks that an empty string can be encrypted and decrypted without error,
   * confirming handling of empty input.
   */
  @Test
  fun `empty string can be encrypted and decrypted`() {
    val emptyData = ""
    val encrypted = cryptoManager.encrypt(emptyData, testKeyAlias)
    val decrypted = cryptoManager.decrypt(encrypted, testKeyAlias)
    assertEquals(emptyData, decrypted)
  }
}