package com.example.journalapp

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp

data class Journal(
    val title: String,
    val thoughts: String,
    val imageUrl: String,
    val userId: String,
    val timeAdded: Timestamp,
    var username: String,
) {
    object DataBindingAdapter {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun setImageByRes(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)

        }
    }

}