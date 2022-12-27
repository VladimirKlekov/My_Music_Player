//package ru.netology.musicplayer.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import ru.netology.musicplayer.databinding.MusicServerViewBinding
//
//
//class MusicServerAdapter(private val context: Context, private val musicListServer: ArrayList<MusicJson>) :
//    RecyclerView.Adapter<MusicServerAdapter.MyHolder>() {
//    /** для управления music_view */
//    class MyHolder(binding: MusicServerViewBinding) : RecyclerView.ViewHolder(binding.root) {
//        val nameSong = binding.nameSongSMV
//        val title = binding.titleSMV
//        val root = binding.root
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
//        /** передам контекст MV родителя */
//        return MusicServerAdapter.MyHolder(
//            MusicServerViewBinding.inflate(
//                LayoutInflater.from(context),
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: MyHolder, position: Int) {
//        holder.nameSong = musicListServer[position].tracks
//    }
//
//    override fun getItemCount(): Int {
//        return musicListServer.size
//    }
//
//
//}