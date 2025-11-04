package com.volleylord.core.data.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.volleylord.core.domain.models.Photo
import com.volleylord.core.domain.services.PhotoDownloader
import javax.inject.Inject

class DefaultPhotoDownloader @Inject constructor() : PhotoDownloader {
  override suspend fun download(context: Context, photo: Photo) {
    val imageUrl = photo.largeImageUrl ?: photo.thumbnailUrl ?: return

    val fileName = "pexels_${photo.id}.jpg"

    val request = DownloadManager.Request(Uri.parse(imageUrl))
      .setTitle(fileName)
      .setDescription("Downloading photo")
      .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
      .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
      .setAllowedOverMetered(true)
      .setAllowedOverRoaming(true)

    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    dm.enqueue(request)
  }
}


