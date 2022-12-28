package ru.netology.musicplayer.service

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import ru.netology.musicplayer.PlayerActivity
import ru.netology.musicplayer.R
import ru.netology.musicplayer.application.ApplicationClass

class MusicService : Service() {
    private val myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null

    //Для меню-увед с кнопкамию Получаю элементы управления транспортом, мультимедийные кнопки и команды от контроллеров и системы
    private lateinit var mediaSession : MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My music")
        return myBinder
    }

    /**внутренний класс для доступа
     * Binder - это простой RecyclerView с привязкой к данным и шаблоном  */
    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    /**меню уведомления с кнопками*/
    fun showNotification() {
        //уведомление при старте. установить как минимум smallIcon, contentTitle и contentText. Если пропустить одно, уведомление не будет отображаться
        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.music_player_icon_slash_screen
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon, "Previous", null)
            .addAction(R.drawable.play_icon, "Play", null)
            .addAction(R.drawable.next_icon, "Next", null)
            .addAction(R.drawable.exit_icon, "Exit", null)
            .build()
        //предоставляя пользователю текущее уведомление, которое будет отображаться в этом состоянии.
        startForeground(13, notification)
    }
}