package com.example.cpu02351_local.firebasechatapp

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class GlideDataBinder {

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun setImageUrl(imageView: ImageView, url: String) {
            val context = imageView.context
            val options = RequestOptions()
            options.centerCrop()
            options.sizeMultiplier(0.3f)
            Glide.with(context).load(url)
                    .thumbnail(0.25f)
                    .apply(options)
                    .into(imageView)
        }

        fun setImageUrl(imageView: ImageView, url: Uri) {
            val context = imageView.context
            val options = RequestOptions()
            options.centerCrop()
            options.sizeMultiplier(0.3f)
            Glide.with(context).load(url)
                    .thumbnail(0.25f)
                    .apply(options)
                    .into(imageView)
        }


    }
}
