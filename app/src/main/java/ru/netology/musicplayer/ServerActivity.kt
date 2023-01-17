package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.musicplayer.adapter.MusicServerAdapter
import ru.netology.musicplayer.databinding.ActivityServerBinding
import ru.netology.musicplayer.dto.*

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerBinding
    private var musicServerAdapter: MusicServerAdapter? = null
    private var mediaServerPlayer: MediaPlayer? = null
    private val urlSong = "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"
    //плай-пауза
    var isPlayingServer: Boolean = false
    //музыкальная позиция
    var songPositionServer:Int =0
    //список трэков и id
    lateinit var musicListSA: ArrayList<Track>

    companion object {
        var musicJsonServer: MusicJson? = null
        }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setTheme(R.style.coolPick)
        setContentView(binding.root)
        musicListSA = ArrayList()

        val tempList = ArrayList<Track>()

        musicJsonServer?.tracks?.forEach { element ->
            tempList.add(element)
            val x =element.copy(
                id= element.id,
                file = urlSong+element.file
            )
        musicListSA.add(x)
        }

        createMediaPlayer()

        musicServerAdapter?.updateMusicList(serverList = musicListSA)


       binding.previousBtnServer.setOnClickListener {
           --songPositionServer
           createMediaPlayer()
           playMusic()
        }

        binding.nextBtnServer.setOnClickListener {
            ++songPositionServer
            createMediaPlayer()
            playMusic()
        }
        //кнопка назад
        binding.backBtnSA.setOnClickListener {
            finish()
            mediaServerPlayer?.stop()
        }

        binding.playPauseBtnServer.setOnClickListener {
            if(isPlayingServer) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        /**заголовки*/
        binding.titleSA.text = musicJsonServer?.title
        binding.subtitleSA.text = musicJsonServer?.subtitle
        binding.artistSA.text = musicJsonServer?.artist
        binding.publishedSA.text = musicJsonServer?.published
        binding.genreSA.text = musicJsonServer?.genre

        /**для recyclerview в server*/
        binding.musicRVServer.setHasFixedSize(true)
        binding.musicRVServer.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.musicRVServer.layoutManager = LinearLayoutManager(this)//привязка верстки
        musicServerAdapter = MusicServerAdapter(this, tempList) //передача списка музыки в адптер
        binding.musicRVServer.adapter = musicServerAdapter//приравнивание адаптеров
    }

    /** плэй-пауза*/
    private fun playMusic() {
        binding.playPauseBtnServer.setIconResource(R.drawable.pause_icon)
        isPlayingServer = true
        mediaServerPlayer!!.start()
    }

    private fun pauseMusic() {
       binding.playPauseBtnServer.setIconResource(R.drawable.play_icon)
        isPlayingServer = false
        mediaServerPlayer!!.pause()
    }

    /** медиаплеер*/
    fun createMediaPlayer() {
        try {
           if (mediaServerPlayer == null) mediaServerPlayer =
                MediaPlayer()
            mediaServerPlayer!!.reset()
            mediaServerPlayer!!.setDataSource(musicListSA[songPositionServer].file )
            mediaServerPlayer!!.prepare()

        } catch (e: Exception) {
            return
        }
    }

}









