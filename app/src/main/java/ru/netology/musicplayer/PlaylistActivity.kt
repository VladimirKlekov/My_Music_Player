package ru.netology.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.adapter.PlayListViewAdapter
import ru.netology.musicplayer.databinding.ActivityPlaylistBinding
import ru.netology.musicplayer.databinding.AddPlailistDialogBinding
import ru.netology.musicplayer.dto.MusicPlaylist
import ru.netology.musicplayer.dto.Playlist
import ru.netology.musicplayer.dto.exitApplication
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlayListViewAdapter

    companion object{
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        /**список плэйлистов*/
//        val tempList = ArrayList<String>()
//        tempList.add("Play list 1")
//        tempList.add("Play list 2")
//        tempList.add("Play list 3")
//        tempList.add("Play list 4")
//        tempList.add("Play list 5")

        /**RV adapter*/
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.playlistRV.layoutManager =
            GridLayoutManager(this@PlaylistActivity, 2)//привязка верстки
        adapter = PlayListViewAdapter(this, playlistList = musicPlaylist.ref)//передача списка музыки в адптер
        binding.playlistRV.adapter = adapter//приравнивание адаптеров

        //кнопка назад
        binding.backBtnPLA.setOnClickListener {
            finish()
        }
        //кнопка добавить плэйлист
        binding.addPlaylistBtn.setOnClickListener {
            customAlertDialog()
        }
    }

    /**диалоговое окно для смены названия плэйлиста*/
    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlaylistActivity)
            .inflate(R.layout.add_plailist_dialog, binding.root, false)
        val binder = AddPlailistDialogBinding.bind(customDialog)
        //вариант с уведомлением(запросом) пользователя
        //Кроме стандартного диалогового окна AlertDialog можно использовать диалоговое
        // окно в стиле Material Design с помощью класса MaterialAlertDialogBuilder.
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle(R.string.playlist_details)
            .setPositiveButton(R.string.add) { dialog, _ ->
                val playlistName = binder.playlistName.text
                val createBy = binder.yourName.text
                //проверка между строками
                if(playlistName != null && createBy != null){
                    if(playlistName.isNotEmpty() && createBy.isNotEmpty()){
                        addPlaylist(playlistName.toString(),createBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }
    private fun addPlaylist(name: String, createBy: String){
        var playlistExists = false
        for(i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playlistExists = true
                break
            }
        }
        //проверка плэйлиста
        if(playlistExists) Toast.makeText(this, R.string.playlist_exists, Toast.LENGTH_SHORT).show()
        else {
            val tempPlayList = Playlist()
            tempPlayList.name = name
            tempPlayList.playlist = ArrayList()
            tempPlayList.createdBy = createBy
            val calendar = java.util.Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
            tempPlayList.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlayList)
            adapter.refreshPlaylist()
        }
    }
}