package ru.netology.musicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.musicplayer.databinding.MusicServerViewBinding

class MusicServerAdapter(private val context: Context, private val musicListServer:ArrayList<String>) :
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


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyServerHolder, position: Int) {
        musicListServer.apply {
            notifyDataSetChanged()
        }
        holder.name.text = musicListServer[position]

    }

    override fun getItemCount(): Int {

        return musicListServer.size
    }
}









