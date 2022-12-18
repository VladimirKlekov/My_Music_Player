package ru.netology.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.musicplayer.PlayerActivity
import ru.netology.musicplayer.R
import ru.netology.musicplayer.databinding.MusicViewBinding
import ru.netology.musicplayer.dto.Music
import ru.netology.musicplayer.dto.formatDuration

class MusicAdapter(private val context: Context, private val musicList: ArrayList<Music>) :
    RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    /** для управления music_view */
    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
        /** передам контекст MV родителя */
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        /** отображение экрана просмотра музыки - список музыки */
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        //holder.duration.text = musicList[position].duration.toString()//добавил форматирование из функцию из data class Music->
        holder.duration.text = formatDuration(musicList[position].duration)
        //подгрузка иконок
        Glide.with(context)
            .load(musicList)
            .apply (RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(holder.image)
        //продолж
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
        //для class PlayerActivity
            intent.putExtra("index", position)
            intent.putExtra("class","MusicAdapter")
            //________
            ContextCompat.startActivity(context, intent, null)
        }
        }

    override fun getItemCount(): Int {
        return musicList.size
    }

}