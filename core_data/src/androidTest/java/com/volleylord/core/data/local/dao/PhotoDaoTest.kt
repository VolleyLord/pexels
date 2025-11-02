package com.volleylord.core.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.volleylord.core.data.local.database.AppDatabase
import com.volleylord.core.data.local.entities.PhotoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PhotoDaoTest {

  private lateinit var photoDao: PhotoDao
  private lateinit var db: AppDatabase

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(
      context, AppDatabase::class.java
    ).build()
    photoDao = db.photoDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun insertAndGetPhoto() = runBlocking {
    val photo = PhotoEntity(1, 100, 100, "url", "Dani", "url", 1L, "#FFFFFF", "thumb", "tiny", "large", "alt")
    photoDao.insertPhotos(listOf(photo))
    val byId = photoDao.getPhotoById(1)
    assertNotNull(byId)
    assertEquals(photo.photographer, byId?.photographer)
  }

  @Test
  @Throws(Exception::class)
  fun getAllPhotos() = runBlocking {
    val photo1 = PhotoEntity(1, 100, 100, "url", "Dani", "url", 1L, "#FFFFFF", "thumb", "tiny", "large", "alt")
    val photo2 = PhotoEntity(2, 100, 100, "url", "Tejada", "url", 2L, "#FFFFFF", "thumb", "tiny", "large", "alt")
    photoDao.insertPhotos(listOf(photo1, photo2))
    val allPhotos = photoDao.getAllPhotos().first()
    assertEquals(2, allPhotos.size)
  }

  @Test
  @Throws(Exception::class)
  fun clearAllPhotos() = runBlocking {
    val photo1 = PhotoEntity(1, 100, 100, "url", "Dani", "url", 1L, "#FFFFFF", "thumb", "tiny", "large", "alt")
    photoDao.insertPhotos(listOf(photo1))
    photoDao.clearAllPhotos()
    val allPhotos = photoDao.getAllPhotos().first()
    assertTrue(allPhotos.isEmpty())
  }
}