package com.bignerdranch.android.photogallery

import android.os.HandlerThread
import android.util.Log

private const val TAG = "ThumbnailDownloader"

class ThumbnailDownloader<in T>: HandlerThread(TAG) {

    private var hasQuit = false

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
    }
}