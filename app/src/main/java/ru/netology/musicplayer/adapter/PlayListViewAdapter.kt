package ru.netology.musicplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.musicplayer.databinding.PlaylistViewBinding

class PlayListViewAdapter(private val context: Context, private var playlist: ArrayList<String>) :
    RecyclerView.Adapter<PlayListViewAdapter.MyHolder>() {

    /** для управления music_view */
    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = playlist[position]
        holder.name.isSelected = true
    }

    override fun getItemCount(): Int {
        return playlist.size
    }
}