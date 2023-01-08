package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.musicplayer.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {

companion object{
    @SuppressLint("StaticFieldLeak")//
    lateinit var binding: FragmentNowPlayingBinding
}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        /**кнопки*/
        binding.playPauseBtnNP.setOnClickListener {
            if(PlayerActivity.isPlaying == true) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            //картинка
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(binding.songImgNP)
            //заголовок песни
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            //смена иконки плэй-пауза
            if (PlayerActivity.isPlaying == true) {
                binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            }else {
                binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
            }


        }
    }
    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
    }
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
    }

}