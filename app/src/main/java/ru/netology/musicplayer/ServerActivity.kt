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
import java.io.IOException
import java.util.concurrent.TimeUnit

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerBinding
    private lateinit var musicServerAdapter: MusicServerAdapter
    var musicJson: MusicJson? = null


    /*********************************************************************************************/
    val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    val client = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
     /*********************************************************************************************/
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityServerBinding.inflate(layoutInflater)
         setContentView(binding.root)
         run()
         //кнопка назад
         binding.backBtnSA.setOnClickListener {
             finish()
         }
         //val tempList = listOf<Track>()
         val tempList = ArrayList<String>()
         musicJson?.tracks?.forEach { element ->
             val score = element.file
             tempList.add(score)
         }
         println("tracks" + tempList)
        /**для recyclerview в server*/
        binding.musicRVServer.setHasFixedSize(true)
        binding.musicRVServer.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.musicRVServer.layoutManager = LinearLayoutManager(this)//привязка верстки
        musicServerAdapter = MusicServerAdapter(this, tempList) //передача списка музыки в адптер
        binding.musicRVServer.adapter = musicServerAdapter//приравнивание адаптеров
    }
    /*********************************************************************************************/
    fun run() {
        val url = " https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json"
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response){
                val test = response.body?.string().toString()
               musicJson= GsonBuilder().create().fromJson(test, MusicJson::class.java)
              //println(musicJson)

            }
        })
    }
    }




//musicJson.addAll(
//listOf(
//GsonBuilder().create().fromJson(test, MusicJson::class.java)
//
//)
//)









