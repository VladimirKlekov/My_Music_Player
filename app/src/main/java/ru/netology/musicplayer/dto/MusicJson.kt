package ru.netology.musicplayer.dto

import com.google.gson.annotations.SerializedName

data class MusicJson(
    @SerializedName("id")val id: Int,
    @SerializedName("title")val title: String,
    @SerializedName("subtitle")val subtitle: String,
    @SerializedName("artist")val artist: String,
    @SerializedName("published")val published: String,
    @SerializedName("genre")val genre: String,
    @SerializedName("tracks")val tracks: List<Track>
)




