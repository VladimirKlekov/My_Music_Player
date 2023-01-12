package ru.netology.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.adapter.PlayListViewAdapter
import ru.netology.musicplayer.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlaylistBinding
    private lateinit var adapter:PlayListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tempList = ArrayList<String>()
        tempList.add("Travel Song")
        tempList.add("Lets Enjoy the Music")
        tempList.add("My playlist")
        tempList.add("Travel Song for Train")
        tempList.add("Travel Song for Bus")
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.playlistRV.layoutManager = GridLayoutManager(this@PlaylistActivity, 2)//привязка верстки
        adapter = PlayListViewAdapter(this, tempList)//передача списка музыки в адптер
        binding.playlistRV.adapter = adapter//приравнивание адаптеров

        //кнопка назад
        binding.backBtnPLA.setOnClickListener{
            finish()
        }
    }
}