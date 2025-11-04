package com.volleylord.pexels.downloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.widget.Toast

class DownloadCompletedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
      val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
      if (downloadId != -1L) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        dm.query(query)?.use { cursor: Cursor ->
          if (cursor.moveToFirst()) {
            val statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusIdx)
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
              Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
            } else if (status == DownloadManager.STATUS_FAILED) {
              Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
            }
          }
        }
      }
    }
  }
}


