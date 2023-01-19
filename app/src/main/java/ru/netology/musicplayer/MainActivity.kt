package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.BuildConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.musicplayer.MainActivity.Companion.MusicListMA
import ru.netology.musicplayer.MainActivity.Companion.currentGradient
import ru.netology.musicplayer.MainActivity.Companion.currentThemeNav
import ru.netology.musicplayer.MainActivity.Companion.search
import ru.netology.musicplayer.MainActivity.Companion.themeIndex
import ru.netology.musicplayer.ServerActivity.Companion.musicJsonServer
import ru.netology.musicplayer.adapter.MusicAdapter
import ru.netology.musicplayer.databinding.ActivityMainBinding
import ru.netology.musicplayer.dto.Music
import ru.netology.musicplayer.dto.MusicJson
import ru.netology.musicplayer.dto.MusicPlaylist
import ru.netology.musicplayer.dto.exitApplication
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    /**перечень Music*/
    companion object {
        lateinit var MusicListMA: ArrayList<Music>
        //поиск музыки
        lateinit var musicListSearch: ArrayList<Music>
        var search: Boolean = false
        var themeIndex: Int = 0
        val currentTheme = arrayOf(R.style.coolPink, R.style.coolBlue,  R.style.coolPurple, R.style.coolGreen, R.style.coolBlack)
        val currentThemeNav = arrayOf(R.style.coolPinkNav, R.style.coolBlueNav,  R.style.coolPurpleNav, R.style.coolGreenNav, R.style.coolBlackNav)
        val currentGradient = arrayOf(R.drawable.gradient_pink, R.drawable.gradient_blue, R.drawable.gradient_purple, R.drawable.gradient_green,
            R.drawable.gradient_black)
        var sortOrder: Int = 0
        val sortingList = arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC")

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //nawdraw
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        //requestRuntimePermission()//глючит разрешение
        setTheme(currentThemeNav[themeIndex])
        //вернул из private fun initializeLayout() из-за багов
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /**для nav drawer выезжающая навигационная панель из меню*/
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission()) {
            initializeLayout()

            /**json для получения списка любимых песен*/
            FavouriteActivity.favouriteSong = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if(jsonString != null){
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouriteActivity.favouriteSong.addAll(data)
            }

            /**json для плэй листа*/
            PlaylistActivity.musicPlaylist = MusicPlaylist()
            val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
            if(jsonStringPlaylist != null){
                val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
                PlaylistActivity.musicPlaylist = dataPlaylist
            }

        }

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
                R.id.navFeedback ->
                    startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))

//                    Toast.makeText(
//                    baseContext,
//                    (R.string.feedback),
//                    Toast.LENGTH_SHORT
//                ).show()
                R.id.navSetting ->
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
//                    Toast.makeText(
//                    baseContext,
//                    (R.string.setting),
//                    Toast.LENGTH_SHORT
//                ).show()
                R.id.navAbout ->
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
//                    Toast.makeText(baseContext, (R.string.about), Toast.LENGTH_SHORT)
//                    .show()
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
        /**для получения музыки с сервера*/
        getJsonServer()
        /**для recyclerview в activity_main*/
        search = false
        //для сортировки
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder", 0)
        MusicListMA = getAllAudio()
//        val musicList = ArrayList<String>()//список музыки
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
             sortingList[sortOrder])
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


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        /**json сохранение списка любимых песен*/
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSong)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
        //для сортировки
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue = sortEditor.getInt("sortOrder", 0)
        if(sortOrder != sortValue){
            sortOrder = sortValue
            MusicListMA = getAllAudio()
            musicAdapter.updateMusicList(MusicListMA)
        }
        if(PlayerActivity.musicService != null) binding.nowPlaying.visibility = View.VISIBLE
    }

    /**Поиск*/
    //https://developer.alexanderklimov.ru/android/theory/menu.php
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        //для загрузки градиентов
        findViewById<LinearLayout>(R.id.linearLayoutNav)?.setBackgroundResource(currentGradient[themeIndex])
        // импортировал as import androidx.appcompat.widget.SearchView
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        //объект прослушивателя, который получает обратные вызовы, когда пользователь выполняет
        // действия в SearchView, такие как нажатие на кнопки или ввод запроса.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    search = true
                    musicAdapter.updateMusicList(searchList = musicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    /**Клиет для получения json с сервера*/
    fun getJsonServer(){
//            val logging = HttpLoggingInterceptor().apply {
//                if (BuildConfig.DEBUG) {
//                    level = HttpLoggingInterceptor.Level.BODY
//                }
//            }
            val client = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            val url = " https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json"
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response){
                    val test = response.body?.string().toString()
                    musicJsonServer= GsonBuilder().create().fromJson(test, MusicJson::class.java)
                    //println(musicJson)
                }
            })

        }
    }




