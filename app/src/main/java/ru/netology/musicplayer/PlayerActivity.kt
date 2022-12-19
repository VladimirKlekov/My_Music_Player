package ru.netology.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.musicplayer.databinding.ActivityPlayerBinding
import ru.netology.musicplayer.dto.Music

class PlayerActivity : AppCompatActivity() {

    /**перечень Music*/
    companion object {
        lateinit var musicListPA: ArrayList<Music>
        //музыкальная позиция
        var songPosition: Int = 0
        //проигрыватель
        var mediaPlayer: MediaPlayer? = null
        //плай-пауза
        var isPlaying: Boolean = false
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                createMediaPlayer()
            }
            "MainActivity" ->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                //случайный порядок
                musicListPA.shuffle()
                //плэй-пауза
                setLayout()
                //медиаплеер
                createMediaPlayer()
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
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
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
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        isPlaying = false
        mediaPlayer!!.pause()
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

}