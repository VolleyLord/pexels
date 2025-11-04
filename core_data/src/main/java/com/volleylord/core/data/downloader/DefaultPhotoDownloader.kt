package com.volleylord.core.data.downloader

import android.content.Context
import android.os.Build
import android.os.Environment
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.services.PhotoDownloader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

class DefaultPhotoDownloader @Inject constructor() : PhotoDownloader {
  override suspend fun download(context: Context, photo: Photo) {
    val imageUrl = photo.largeImageUrl ?: photo.thumbnailUrl ?: return

    val fileName = "pexels_${photo.id}.jpg"
    val file: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        ?: context.filesDir
      File(downloadsDir, fileName)
    } else {
      val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      if (!downloadsDir.exists()) downloadsDir.mkdirs()
      File(downloadsDir, fileName)
    }

    val url = URL(imageUrl)
    val connection = url.openConnection()
    connection.connect()

    val input: InputStream = connection.getInputStream()
    val output = FileOutputStream(file)

    input.use { inp ->
      output.use { out -> inp.copyTo(out) }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      android.media.MediaScannerConnection.scanFile(
        context,
        arrayOf(file.absolutePath),
        null,
        null
      )
    }
  }
}


