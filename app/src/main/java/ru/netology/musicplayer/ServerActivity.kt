package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.musicplayer.dto.MusicJson
import ru.netology.musicplayer.adapter.MusicServerAdapter
import ru.netology.musicplayer.databinding.ActivityServerBinding
import ru.netology.musicplayer.dto.Track
import java.io.IOException
import java.util.concurrent.TimeUnit

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerBinding
    private var musicServerAdapter: MusicServerAdapter? = null

    companion object{
        var musicJsonServer: MusicJson? = null
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityServerBinding.inflate(layoutInflater)
         setContentView(binding.root)

         //кнопка назад
         binding.backBtnSA.setOnClickListener {
             finish()
         }

         val tempList = ArrayList<Track>()
         musicJsonServer?.tracks?.forEach { element ->
            // val score = element.file
             tempList.add(element)
         }

         musicServerAdapter?.updateMusicList(serverList = tempList)
         println("tracks" + tempList)
        /**для recyclerview в server*/
        binding.musicRVServer.setHasFixedSize(true)
        binding.musicRVServer.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.musicRVServer.layoutManager = LinearLayoutManager(this)//привязка верстки
        musicServerAdapter = MusicServerAdapter(this, tempList) //передача списка музыки в адптер
        binding.musicRVServer.adapter = musicServerAdapter//приравнивание адаптеров
    }

    }









