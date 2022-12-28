package ru.netology.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.musicplayer.adapter.MusicJson
import ru.netology.musicplayer.databinding.ActivityServerBinding
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ServerActivity : AppCompatActivity() {

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
        binding.musicRVServer.setRecyclerListener { request }

    }

    /**JSON*/

    fun json() {

        val musicListJson = object : TypeToken<List<MusicJson>>() {}

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
                        gson.fromJson(it, musicListJson.type)
                    }
            } catch (e: IOException) {
            }

        }
    }
}