package ru.netology.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.musicplayer.adapter.FavouriteAdapter
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.databinding.ActivityFavouriteBinding
import ru.netology.musicplayer.dto.Music
import ru.netology.musicplayer.dto.checkPlaylist

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter

    companion object {
        var favouriteSong: ArrayList<Music> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favouriteSong = checkPlaylist(favouriteSong)
        //для recyclerview
//        val tempList = ArrayList<String>()
//        tempList.add("Song 1")
//        tempList.add("Song 2")
//        tempList.add("Song 3")
//        tempList.add("Song 4")
//        tempList.add("Song 5")
//        tempList.add("Song 6")
//        tempList.add("Song 7")
        //кнопка назад
        binding.backBtnFA.setOnClickListener{
            finish()
        }
        /**для recyclerview в favourite_view*/
        //https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/GridLayoutManager
        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.favouriteRV.layoutManager = GridLayoutManager(this, 4)//привязка верстки
        adapter = FavouriteAdapter(this, favouriteSong)//передача списка музыки в адптер
        binding.favouriteRV.adapter = adapter//приравнивание адаптеров

        /**кнопка перемешивания в favourite_view*/
        if(favouriteSong.size<1)
            binding.shuffleBtnFA.visibility = View.INVISIBLE
            binding.shuffleBtnFA.setOnClickListener{
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class","FavouriteShuffle")
                startActivity(intent)
            }

        }

    }
