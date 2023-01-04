package ru.netology.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.musicplayer.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //кнопка назад
        binding.backBtnFA.setOnClickListener{
            finish()
        }
    }
}