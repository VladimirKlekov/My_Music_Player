package ru.netology.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.adapter.MusicJson
import ru.netology.musicplayer.adapter.MusicServerAdapter
import ru.netology.musicplayer.adapter.Track
import ru.netology.musicplayer.databinding.ActivityServerBinding
import ru.netology.musicplayer.dto.Music
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ServerActivity : AppCompatActivity() {
    private lateinit var musicServerAdapter: MusicServerAdapter


    /**перечень Music*/
    companion object {

    }

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

    private lateinit var binding: ActivityServerBinding
    private lateinit var request: Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        json()
        dataRecyclerView()
       binding.musicRVServer.setRecyclerListener { request }

    }

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

    private fun dataRecyclerView() {
        /**для recyclerview в */
        val musicList = ArrayList<String>()//список музыки
//        ServerActivity.MusicListServer
        musicList.add("1 Song")
        musicList.add("2 Song")
        musicList.add("3 Song")
        musicList.add("4 Song")
        musicList.add("5 Song")
        binding.musicRVServer.setHasFixedSize(true)
        binding.musicRVServer.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.musicRVServer.layoutManager = LinearLayoutManager(this@ServerActivity)//привязка верстки


    }
}