package ru.netology.musicplayer.dto

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("file")val `file`: String,
    @SerializedName("id")val id: Int
)