package com.example.cpu02351_local.firebasechatapp.previewscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.example.cpu02351_local.firebasechatapp.R

class VideoPreviewActivity : AppCompatActivity(), OnPreparedListener {

    companion object {
        private const val THUMBNAIL_URL = "thumbnail"
        private const val VIDEO_URL = "video"

        @JvmStatic
        fun videoPreviewIntent(context: Context, thumbnailUrl: String, videoUrl: String) : Intent {
            val intent = Intent(context, VideoPreviewActivity::class.java)
            intent.putExtra(THUMBNAIL_URL, thumbnailUrl)
            intent.putExtra(VIDEO_URL, videoUrl)
            return intent
        }
    }

    private lateinit var mVideoUrl: String
    private lateinit var mThumbnail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_preview)
        mVideoUrl = intent.getStringExtra(VIDEO_URL)
        mThumbnail = intent.getStringExtra(THUMBNAIL_URL)
        videoView = findViewById(R.id.videoView)
    }

    private lateinit var videoView: VideoView

    private fun setupVideoView() {
        videoView.setOnPreparedListener(this)
        videoView.setPreviewImage(Uri.parse(mThumbnail))
        videoView.setVideoPath(mVideoUrl)
    }

    private var currentTime = 0L
    override fun onStart() {
        super.onStart()
        setupVideoView()
    }

    override fun onStop() {
        super.onStop()
        currentTime = videoView.currentPosition
    }

    override fun onPrepared() {
        videoView.seekTo(currentTime)
        videoView.videoControlsCore!!.finishLoading()
        videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
