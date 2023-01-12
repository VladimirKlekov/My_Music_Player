package ru.netology.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.adapter.PlayListViewAdapter
import ru.netology.musicplayer.databinding.ActivityPlaylistBinding
import ru.netology.musicplayer.dto.exitApplication

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlayListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**список плэйлистов*/
        val tempList = ArrayList<String>()
        tempList.add("Play list 1")
        tempList.add("Play list 2")
        tempList.add("Play list 3")
        tempList.add("Play list 4")
        tempList.add("Play list 5")

        /**RV adapter*/
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.playlistRV.layoutManager =
            GridLayoutManager(this@PlaylistActivity, 2)//привязка верстки
        adapter = PlayListViewAdapter(this, tempList)//передача списка музыки в адптер
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
        //вариант с уведомлением(запросом) пользователя
        //Кроме стандартного диалогового окна AlertDialog можно использовать диалоговое
        // окно в стиле Material Design с помощью класса MaterialAlertDialogBuilder.
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle(R.string.playlist_details)
            .setPositiveButton(R.string.add) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}