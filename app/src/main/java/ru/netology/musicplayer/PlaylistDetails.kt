package ru.netology.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetails : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding

    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        setContentView(binding.root)
        currentPlaylistPos = intent.extras?.getInt("index") as Int
    }
}