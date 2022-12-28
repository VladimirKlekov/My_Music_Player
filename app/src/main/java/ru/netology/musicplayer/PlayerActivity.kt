package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.musicplayer.databinding.ActivityPlayerBinding
import ru.netology.musicplayer.dto.Music
import ru.netology.musicplayer.service.MusicService

class PlayerActivity : AppCompatActivity(), ServiceConnection {

    /**перечень Music*/
    companion object {
        lateinit var musicListPA: ArrayList<Music>
        //музыкальная позиция
        var songPosition: Int = 0
        //проигрыватель
        var mediaPlayer: MediaPlayer? = null
        //плай-пауза
        var isPlaying: Boolean = false
        //сервис
        var musicService:MusicService? = null
         //TODO Глюки или утечка памяти? добавил анотацию
        //перенес в объект для доступа в NotificationReceiver
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //для запуска сервиса// (Intent) - это механизм для описания одной операции - выбрать фотографию, отправить письмо
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        initializeLayout()

        /**кнопки*/
        binding.previousBtnPA.setOnClickListener {
            prevNextSong(click = false)
        }

        binding.nextBtnPA.setOnClickListener {
            prevNextSong(click = true)
        }

        binding.playPauseBtnPA.setOnClickListener{
            if(isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
    }
    /**для override fun onCreate(savedInstanceState: Bundle?) вынес в функцию, что бы не мешало */
    fun initializeLayout(){
        //получаю данные из class MusicAdapter
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MusicAdapter"->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                //плэй-пауза
                setLayout()
                //медиаплеер
                //createMediaPlayer() - вынес в сервис
            }
            "MainActivity" ->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                //случайный порядок
                musicListPA.shuffle()
                //плэй-пауза
                setLayout()
                //медиаплеер
                //createMediaPlayer() - вынес в сервис
            }
        }
    }

    /** подгрузка главной иконки*/
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPA[songPosition].title
    }

    /** медиаплеер*/
    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            //плэй-пауза смена кнопки
            isPlaying = true
            binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        }catch (e:Exception){
            return
        }
    }

    /** плэй-пауза*/
    private fun playMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        //для меню-уведомления
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        //для меню-уведомления
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    /** кнопки вперед - назад*/
    private fun prevNextSong(click: Boolean){
        if(click){
            //позиция увеличена
            //++songPosition
            setSongPosition(click = true)
            setLayout()
            createMediaPlayer()
        }else{
        //позиция уменьшена
        //--songPosition
        setSongPosition(click = false)
        setLayout()
        createMediaPlayer()
        }
    }
      //TODO
    private fun setSongPosition(click:Boolean){
        if(click){
            if(musicListPA.size -1 == songPosition) {
                songPosition = 0
            }else ++songPosition
        } else {
            if(0 == songPosition) {
                songPosition = musicListPA.size - 1
            }else --songPosition
        }
    }

    /**для class MusicService и Binder*/
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
       val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
//уведомления
        musicService!!.showNotification(R.drawable.pause_icon)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

}