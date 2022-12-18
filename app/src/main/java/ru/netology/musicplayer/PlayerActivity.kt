package ru.netology.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.musicplayer.databinding.ActivityPlayerBinding
import ru.netology.musicplayer.dto.Music

class PlayerActivity : AppCompatActivity() {

    /**перечень Music*/
    companion object {
        lateinit var MusicListPA: ArrayList<Music>
        var songPosition: Int = 0
        //проигрыватель
        var mediaPlayer: MediaPlayer? = null
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //получаю данные из class MusicAdapter
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MusicAdapter"->{
                MusicListPA = ArrayList()
                MusicListPA.addAll(MainActivity.MusicListMA)
                if(mediaPlayer == null) mediaPlayer = MediaPlayer()
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource( MusicListPA[songPosition].path)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()

            }
        }
    }
}