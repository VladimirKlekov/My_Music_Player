package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.databinding.ActivityMainBinding
import ru.netology.musicplayer.dto.Music
import ru.netology.musicplayer.dto.exitApplication
import java.io.File

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    /**перечень Music*/
    companion object {
        lateinit var MusicListMA: ArrayList<Music>
           }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestRuntimePermission()//глючит разрешение
        setTheme(R.style.coolPinkNav)
        //вернул из private fun initializeLayout() из-за багов
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**для nav drawer выезжающая навигационная панель из меню*/
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission())
        initializeLayout()

        /**кнопки*/
        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)
        }

        binding.favouritesBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
            startActivity(intent)
        }

        binding.playlistBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlaylistActivity::class.java)
            startActivity(intent)
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> Toast.makeText(
                    baseContext,
                    (R.string.feedback),
                    Toast.LENGTH_SHORT
                ).show()
                R.id.navSetting -> Toast.makeText(
                    baseContext,
                    (R.string.setting),
                    Toast.LENGTH_SHORT
                ).show()
                R.id.navAbout -> Toast.makeText(baseContext, (R.string.about), Toast.LENGTH_SHORT)
                    .show()
               // R.id.navExit -> exitProcess(1) вариант без уведомления пользователя
                R.id.navExit -> {
                    //вариант с уведомлением(запросом) пользователя
                    //Кроме стандартного диалогового окна AlertDialog можно использовать диалоговое
                    // окно в стиле Material Design с помощью класса MaterialAlertDialogBuilder.
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle(R.string.exit)
                        .setMessage(R.string.question_close_app)
                        .setPositiveButton(R.string.yes){ _, _->
                            exitApplication()
                        }
                        .setNegativeButton(R.string.no){dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    //показать
                    customDialog.show()
                    //Появляются кнопки в всплывающем меню: да и нет
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }

        binding.playServerBtn.setOnClickListener{
            val intent = Intent(this@MainActivity, ServerActivity::class.java)
            startActivity(intent)
        }
    }

    /** для получение дотупа фото, мультимедиа, файлам на устройстве*/
    private fun requestRuntimePermission():Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)//для подавления ошибки MusicListMA = getAllAudio()
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, (R.string.permission_granted), Toast.LENGTH_SHORT).show()
                //MusicListMA = getAllAudio()
                initializeLayout()
            }else ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
        }
    }

    /**для nav drawer */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    /**для override fun onCreate(savedInstanceState: Bundle?) вынес в функцию, что бы не мешало */
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")//подавление количество песен (список)
    private fun initializeLayout() {
        /**для recyclerview в activity_main*/
        val musicList = ArrayList<String>()//список музыки
        MusicListMA = getAllAudio()
//        musicList.add("1 Song")//заменил на MusicListMA= getAllAudio()
//        musicList.add("2 Song")
//        musicList.add("3 Song")
//        musicList.add("4 Song")
//        musicList.add("5 Song")
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)//размер кэша для количества музыки
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)//привязка верстки
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)//передача списка музыки в адптер
        binding.musicRV.adapter = musicAdapter//приравнивание адаптеров
        binding.totalSong.text =
            "Total Song : " + musicAdapter.itemCount//список песен равен списку, пробелу или муз адаптеру
    }

    /**data class Music получение аудио*/
    @SuppressLint("Recycle", "Range")//анатация помогла решить проблему
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music>{
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,selection,
            null,
             null)
        if(cursor != null){
            if(cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")

                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUri = artUriC
                        )
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)
                }while (cursor.moveToNext())
            cursor.close()
        }

        return tempList
    }


    /**кнопка возврат*/
    //жизненый цикл активити https://developer.alexanderklimov.ru/android/theory/fragment-lifecycle.php
    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            exitApplication()
        }
    }

    /**Поиск*/
    //https://developer.alexanderklimov.ru/android/theory/menu.php
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        // импортировал as import androidx.appcompat.widget.SearchView
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        //объект прослушивателя, который получает обратные вызовы, когда пользователь выполняет
        // действия в SearchView, такие как нажатие на кнопки или ввод запроса.
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(this@MainActivity, newText.toString(), Toast.LENGTH_SHORT).show()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

   }



