package ru.netology.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.musicplayer.adapter.FavouriteAdapter
import ru.netology.musicplayer.dto.MusicJson
import ru.netology.musicplayer.adapter.MusicServerAdapter
import ru.netology.musicplayer.databinding.ActivityFavouriteBinding
import ru.netology.musicplayer.dto.Track
import ru.netology.musicplayer.databinding.ActivityServerBinding
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerBinding
    private lateinit var musicServerAdapter: MusicServerAdapter
    private lateinit var request: Request

    /*********************************************************************************************/

    val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    val gson = Gson()

    /*********************************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //для recyclerview
        val tempList = ArrayList<String>()
        tempList.add("Song 1")
        tempList.add("Song 2")
        tempList.add("Song 3")
        tempList.add("Song 4")
        tempList.add("Song 5")
        tempList.add("Song 6")
        tempList.add("Song 7")

        binding.musicRVServer.setRecyclerListener { request }
        //кнопка назад
        binding.backBtnSA.setOnClickListener {
            finish()
        }

        /**для recyclerview в favourite_server*/
        binding.musicRVServer.setHasFixedSize(true)
        binding.musicRVServer.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.musicRVServer.layoutManager = GridLayoutManager(this, 4)//привязка верстки
        musicServerAdapter = MusicServerAdapter(this, tempList)//передача списка музыки в адптер
        binding.musicRVServer.adapter = musicServerAdapter//приравнивание адаптеров

    }
    /*********************************************************************************************/

    /**JSON*/

    fun json() {

        val musicListJson = object : TypeToken<List<MusicJson>>() {}
        val musicTrack = object : TypeToken<List<Track>>() {}

        thread {
            try {
                request = Request.Builder()
                    .url(" https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json")
                    .build()

                return@thread client.newCall(request)
                    .execute()
                    .let {
                        it.body?.string() ?: throw RuntimeException("body is null")
                    }
                    .let {
                        gson.fromJson(it, MusicJson::class.java)
                    }
            } catch (e: IOException) {
            }

        }
    }

}