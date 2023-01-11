package ru.netology.musicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.musicplayer.databinding.MusicServerViewBinding
import ru.netology.musicplayer.dto.Track

class MusicServerAdapter(
    private val context: Context,
    private var musicListServer: ArrayList<Track>
) :
    RecyclerView.Adapter<MusicServerAdapter.MyServerHolder>() {

    /** для управления music_view */
    class MyServerHolder(binding: MusicServerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.nameSongServerView

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
        holder.name.text = musicListServer[position].file

    }

    override fun getItemCount(): Int {
        return musicListServer.size
    }

    /** для музыки с сервера */
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(serverList: ArrayList<Track>) {
        musicListServer = ArrayList()
        musicListServer.addAll(serverList)
        notifyDataSetChanged()
    }

}










