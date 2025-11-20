package com.example.eventease.data.remote.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryService(context: Context) {
    private val CLOUD_NAME = "dvwbrl4el"
    private val UPLOAD_PRESET = "eventease"

    init {
        try {
            MediaManager.get()
        } catch (e: IllegalStateException) {
            val config = mapOf(
                "cloud_name" to CLOUD_NAME,
                "unsigned" to UPLOAD_PRESET
            )
            MediaManager.init(context.applicationContext, config)
        }
    }

    suspend fun uploadImage(imageUri: Uri): String = suspendCancellableCoroutine { continuation ->
        MediaManager.get()
            .upload(imageUri)
            .unsigned(UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"] as? String
                    if (imageUrl != null) {
                        continuation.resume(imageUrl)
                    } else {
                        continuation.resumeWithException(Exception("Cloudinary URL not found"))
                    }
                }

                override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                    continuation.resumeWithException(Exception(error.description))
                }

                override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                }

                override fun onStart(requestId: String) {
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                }
            })
            .dispatch()
    }
}