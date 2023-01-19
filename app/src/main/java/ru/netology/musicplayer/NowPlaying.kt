package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.musicplayer.databinding.FragmentNowPlayingBinding
import ru.netology.musicplayer.dto.setSongPosition

class NowPlaying : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")//
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        /**кнопки*/
        binding.playPauseBtnNP.setOnClickListener {
            if (PlayerActivity.isPlaying == true) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.nextBtnNP.setOnClickListener {
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createMediaPlayer()
            //картинка для NowPlaying
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                )
                .into(NowPlaying.binding.songImgNP)
            //заголовок
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            //воспроизвести
            playMusic()
        }

        //переход к текущей песне
        binding.root.setOnClickListener{
            //requireContext()- экземпляр контекста. возвращает ненулевое значение Context или выдает исключение, если оно недоступно.
            //https://question-it.com/questions/3127142/raznitsa-mezhdu-getcontext-i-requirecontext-pri-ispolzovanii-fragmentov
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            //________
            ContextCompat.startActivity(requireContext(), intent, null)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            //Прокрука текста названия песни
            binding.songNameNP.isSelected = true
            //картинка
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                )
                .into(binding.songImgNP)
            //заголовок песни
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            //смена иконки плэй-пауза
            if (PlayerActivity.isPlaying == true) {
                binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            } else {
                binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
            }


        }
    }

    private fun playMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
    }

}