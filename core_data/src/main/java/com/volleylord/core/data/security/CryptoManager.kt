package com.volleylord.core.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages encryption and decryption of sensitive data using the Android KeyStore. Used
 * for securing API keys.internally by [..local.preferences.SecurePreferencesDataSource] to
 * protect sensitive preferences.”
 */
@Singleton
class CryptoManager(private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
  load(null)
}) {

  /**
   * Retrieves or creates a secret key for the given alias.
   *
   * @param keyAlias The alias for the key in the Android KeyStore.
   * @return The [SecretKey] for encryption/decryption.
   */
  private fun getOrCreateSecretKey(keyAlias: String): SecretKey {
    val existingKey = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
    return existingKey?.secretKey ?: createKey(keyAlias)
  }

  /**
   * Creates a new secret key for the given alias.
   *
   * @param keyAlias The alias for the key in the Android KeyStore.
   * @return The newly created [SecretKey].
   */
  private fun createKey(keyAlias: String): SecretKey {
    return KeyGenerator.getInstance(ALGORITHM).apply {
      init(
        KeyGenParameterSpec.Builder(
          keyAlias,
          KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
          .setBlockModes(BLOCK_MODE)
          .setEncryptionPaddings(PADDING)
          .setUserAuthenticationRequired(false)
          .setRandomizedEncryptionRequired(true)
          .build()
      )
    }.generateKey()
  }

  /**
   * Encrypts the provided data using the Android KeyStore.
   *
   * @param data The string to encrypt.
   * @param keyAlias The alias for the encryption key.
   * @return An [EncryptedData] object containing the encrypted bytes and initialization vector.
   */
  fun encrypt(data: String, keyAlias: String): EncryptedData {
    val secretKey = getOrCreateSecretKey(keyAlias)
    val cipher = Cipher.getInstance(TRANSFORMATION).apply {
      init(Cipher.ENCRYPT_MODE, secretKey)
    }

    val encryptedBytes = cipher.doFinal(data.toByteArray())
    val iv = cipher.iv

    return EncryptedData(encryptedBytes, iv)
  }

  /**
   * Decrypts the provided encrypted data using the Android KeyStore.
   *
   * @param encryptedData The [EncryptedData] containing the encrypted bytes and initialization vector.
   * @param keyAlias The alias for the decryption key.
   * @return The decrypted string.
   */
  fun decrypt(encryptedData: EncryptedData, keyAlias: String): String {
    val secretKey = getOrCreateSecretKey(keyAlias)
    val cipher = Cipher.getInstance(TRANSFORMATION).apply {
      init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(encryptedData.iv))
    }

    val decryptedBytes = cipher.doFinal(encryptedData.data)
    return String(decryptedBytes)
  }

  companion object {
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
  }
}

/**
 * Represents encrypted data with its initialization vector.
 *
 * @param data The encrypted bytes.
 * @param iv The initialization vector used for encryption.
 */
data class EncryptedData(
  val data: ByteArray,
  val iv: ByteArray
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as EncryptedData
    if (!data.contentEquals(other.data)) return false
    if (!iv.contentEquals(other.iv)) return false
    return true
  }

  override fun hashCode(): Int {
    var result = data.contentHashCode()
    result = 31 * result + iv.contentHashCode()
    return result
  }
}