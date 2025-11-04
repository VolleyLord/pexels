package com.volleylord.core.domain.services

import android.content.Context
import com.volleylord.core.domain.models.Photo

interface PhotoDownloader {
  suspend fun download(context: Context, photo: Photo)
}


