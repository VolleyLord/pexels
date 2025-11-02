package com.volleylord.core.domain.usecases.settings

import com.volleylord.core.domain.repositories.SettingsRepository
import javax.inject.Inject

/**
 * Use case for seeding an initial API key if none is set.
 */
class SeedInitialApiKeyUseCase @Inject constructor(
  private val settingsRepository: SettingsRepository
) {

  /**
   * Seeds an initial API key if none exists in the repository.
   *
   * @throws SecurityException If there is an issue with encryption or secure storage.
   * @throws java.io.IOException If there is an issue accessing the storage.
   */
  suspend operator fun invoke() {
    settingsRepository.seedInitialApiKeyIfNeeded()
  }
}