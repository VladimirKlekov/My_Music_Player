package ru.netology.musicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.musicplayer.*
import ru.netology.musicplayer.databinding.MusicViewBinding
import ru.netology.musicplayer.dto.Music
import ru.netology.musicplayer.dto.formatDuration

class MusicAdapter(private val context: Context, private var musicList: ArrayList<Music>,
                   private var playlistDetails: Boolean = false, private var selectionActivity: Boolean = false)
    :RecyclerView.Adapter<MusicAdapter.MyHolder>() {
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
            .load(musicList[position].artUri)
            .apply (RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(holder.image)
        when {
            playlistDetails->{
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }
            }
            selectionActivity->{
                holder.root.setOnClickListener {
                    if(addSong(musicList[position]))
                holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_pink))

                else
                    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }
            }

                else -> {
                    //продолж
                    holder.root.setOnClickListener {
                        when{
                            //поиск
                            MainActivity.search ->sendIntent(ref = "MusicAdapterSearch", pos = position)
                            //проверка воспроизводимой/текущей песни
                            musicList[position].id == PlayerActivity.nowPlayingId ->
                                sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)

                            else -> sendIntent(ref = "MusicAdapter", pos = position)
                        }
                    }
                }
        }

        }

    override fun getItemCount(): Int {
        return musicList.size
    }

    /** для поиска */
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList : ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    //продолжение. вынес в отдельную функцию
    private fun sendIntent(ref : String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        //для class PlayerActivity
        intent.putExtra("index", pos)
        intent.putExtra("class",ref)
        //________
        ContextCompat.startActivity(context, intent, null)
    }

    /**добавить музыку в плэйлистах*/
    private fun addSong(song: Music): Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }
    /**обновление плэйлиста*/
    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}