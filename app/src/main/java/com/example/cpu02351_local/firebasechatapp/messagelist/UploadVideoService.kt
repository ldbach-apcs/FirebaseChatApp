package com.example.cpu02351_local.firebasechatapp.messagelist

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import com.iceteck.silicompressorr.SiliCompressor
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File

abstract class UploadVideoService : IntentService("UploadVideoService") {

    companion object {
        const val INFO = "upload_info"
    }

    private val handlerForCompressedVideo = object : CompletableObserver {
        override fun onComplete() {
            startUploadingTask()
        }

        override fun onSubscribe(d: Disposable) {
            // disposable.add(d)
        }

        override fun onError(e: Throwable) {
            // Do nothing, service ends itself
        }
    }
    private lateinit var mVideoPath: String
    private lateinit var mInfo: VideoUploadInfo

    override fun onHandleIntent(intent: Intent?) {
        val info = intent?.getSerializableExtra(INFO) as VideoUploadInfo
        mInfo = info
        mMessageId = info.messageId
        val videoPath = info.filePath
        mVideoPath = videoPath
        val storagePath = Environment.getExternalStorageDirectory().path + "/AwesomeChat/Video2/"
        val file = File(storagePath)
        if (!file.exists()) {
            file.mkdirs()
        }

        Completable.fromCallable {
            apply { mVideoPath = SiliCompressor.with(applicationContext).compressVideo(videoPath, storagePath)
            }}
                .subscribeOn(Schedulers.io())
                .subscribe(handlerForCompressedVideo)
    }

    private lateinit var mMessageId: String

    private fun startUploadingTask() {
        val bitmap = generateThumbnail()
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        bitmap?.recycle()

        // Save metadata
        mInfo.videoWidth = bitmap!!.width
        mInfo.videoHeight = bitmap.height
        upload(byteArray, mVideoPath, mInfo)
    }

    abstract fun upload(byteArray: ByteArray, videoPath: String, info: VideoUploadInfo)
    private fun generateThumbnail(): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(mVideoPath)
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable(
                    "Exception while getting thumbnail $mVideoPath ${e.message}")

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release()
            }
        }
        return bitmap
    }

}