package ru.netology.musicplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import ru.netology.musicplayer.PlayerActivity
import ru.netology.musicplayer.R
import ru.netology.musicplayer.application.ApplicationClass
import ru.netology.musicplayer.application.NotificationReceiver
import ru.netology.musicplayer.dto.formatDuration
import ru.netology.musicplayer.dto.getImgArt

class MusicService : Service() {
    private val myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null

    //Для меню-увед с кнопкамию Получаю элементы управления транспортом, мультимедийные кнопки и команды от контроллеров и системы
    private lateinit var mediaSession: MediaSessionCompat
    //
    private lateinit var runnable: Runnable

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
    fun showNotification(playPauseBtn:Int) {
        //действия кнопки в меню-уведомлении
        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //для загрузки изображения
        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if(imgArt != null){
            //Возвращает декодированное растровое изображение или значение null, если изображение не удалось декодировать.
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
            }else{
            BitmapFactory.decodeResource(resources, R.drawable.music_player_icon_slash_screen)
            }

        //уведомление при старте. установить как минимум smallIcon, contentTitle и contentText. Если пропустить одно, уведомление не будет отображаться
        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon, "Previous", prevPendingIntent)
           // .addAction(R.drawable.play_icon, "Play", playPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)//вынес в функцию в NotificationReceiver
            .addAction(R.drawable.next_icon, "Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
            .build()
        //предоставляя пользователю текущее уведомление, которое будет отображаться в этом состоянии.
        startForeground(13, notification)
    }
    /** медиаплеер*/
    fun createMediaPlayer() {
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer =
                MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            //уведомления
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration
        } catch (e: Exception) {
            return
        }
    }
    //runnable https://tedblob.com/android-kotlin-handler-and-runnable-examples/
    fun seekBarSetup(){
           runnable = Runnable {
               PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
               PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
               Handler(Looper.getMainLooper()).postDelayed(runnable,200)
}
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }
}