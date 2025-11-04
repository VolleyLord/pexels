package com.volleylord.core.di

import com.volleylord.core.data.downloader.DefaultPhotoDownloader
import com.volleylord.core.domain.services.PhotoDownloader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloaderModule {
  @Binds
  @Singleton
  abstract fun bindPhotoDownloader(impl: DefaultPhotoDownloader): PhotoDownloader
}


