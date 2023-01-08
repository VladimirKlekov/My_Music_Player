package ru.netology.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.musicplayer.PlayerActivity
import ru.netology.musicplayer.R
import ru.netology.musicplayer.databinding.MusicServerViewBinding
import ru.netology.musicplayer.dto.Track

class MusicServerAdapter(private val context: Context, private val musicListServer: ArrayList<String>) :
    RecyclerView.Adapter<MusicServerAdapter.MyServerHolder>() {

    /** для управления music_view */
    class MyServerHolder(binding: MusicServerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.imageSMV
        val name = binding.nameSongSMV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyServerHolder {
        /** передам контекст MV родителя */
        return MyServerHolder(
            MusicServerViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyServerHolder, position: Int) {
        holder.name.text = musicListServer[position]
//        Glide.with(this)
//            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
//            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
//            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return musicListServer.size
    }
}