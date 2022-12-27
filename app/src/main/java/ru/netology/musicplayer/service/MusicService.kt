package ru.netology.musicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {
    private val myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder {
        return myBinder
    }

    /**внутренний класс для доступа
     * Binder - это простой RecyclerView с привязкой к данным и шаблоном mvvm для andorid listview. */
    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }


}