package com.volleylord.core.data.mappers

import androidx.compose.ui.graphics.Color
import com.volleylord.core.data.local.entities.PhotoEntity
import com.volleylord.core.data.remote.dto.PhotoDto
import com.volleylord.core.data.remote.dto.PhotoSourceDto
import com.volleylord.core.domain.models.Photo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for [PhotoMapper].
 *
 * Verifies that mapping between DTOs, Entities, and Domain models works correctly,
 * including color parsing and URL fields.
 */
@RunWith(RobolectricTestRunner::class)
class PhotoMapperTest {

  private val mapper = PhotoMapper()

  /**
   * Tests that mapping from [PhotoDto] to domain model [Photo]
   * correctly transforms fields including color and nested source URLs.
   */
  @Test
  fun `mapDtoToDomain maps correctly`() {
    val dto = PhotoDto(
      id = 1,
      width = 100,
      height = 200,
      url = "url",
      photographer = "Dani",
      photographerUrl = "photographerUrl",
      photographerId = 1L,
      avgColor = "#FF0000",
      src = PhotoSourceDto(medium = "medium", tiny = "tiny", large = "large"),
      liked = true,
      alt = "alt"
    )

    val domain = mapper.mapDtoToDomain(dto)

    Assert.assertEquals(1, domain.id)
    Assert.assertEquals(Color(0xFFFF0000), domain.avgColor)
    Assert.assertEquals("medium", domain.thumbnailUrl)
    Assert.assertEquals("alt", domain.alt)
  }

  /**
   * Tests that mapping from local database [PhotoEntity] to domain model [Photo]
   * preserves all relevant fields including color and thumbnail URLs.
   */
  @Test
  fun `mapEntityToDomain maps correctly`() {
    val entity = PhotoEntity(
      id = 1,
      width = 100,
      height = 200,
      url = "url",
      photographer = "Dani",
      photographerUrl = "photographerUrl",
      photographerId = 1L,
      avgColor = "#00FF00",
      thumbnailUrl = "thumb",
      tinyThumbnailUrl = "tiny",
      largeImageUrl = "large",
      alt = "alt"
    )

    val domain = mapper.mapEntityToDomain(entity)

    Assert.assertEquals(1, domain.id)
    Assert.assertEquals(Color(0xFF00FF00), domain.avgColor)
    Assert.assertEquals("thumb", domain.thumbnailUrl)
  }

  /**
   * Tests that mapping from domain model [Photo] to local database [PhotoEntity]
   * correctly transforms fields and preserves important properties.
   */
  @Test
  fun `mapDomainToEntity maps correctly`() {
    val domain = Photo(
      id = 1,
      type = "type",
      width = 100,
      height = 200,
      url = "url",
      photographer = "Dani",
      photographerUrl = "photographerUrl",
      photographerId = 1L,
      avgColor = Color.Companion.Blue,
      thumbnailUrl = "thumb",
      tinyThumbnailUrl = "tiny",
      largeImageUrl = "large",
      liked = true,
      alt = "alt"
    )

    val entity = mapper.mapDomainToEntity(domain)
    Assert.assertEquals(1, entity.id)
    Assert.assertNotNull(entity.avgColor)
    Assert.assertEquals("thumb", entity.thumbnailUrl)
  }
}