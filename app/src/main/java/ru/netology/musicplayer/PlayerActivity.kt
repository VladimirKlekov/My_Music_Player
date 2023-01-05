package ru.netology.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.musicplayer.databinding.ActivityPlayerBinding
import ru.netology.musicplayer.dto.Music
import ru.netology.musicplayer.dto.exitApplication
import ru.netology.musicplayer.dto.formatDuration
import ru.netology.musicplayer.dto.setSongPosition
import ru.netology.musicplayer.service.MusicService

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    /**перечень Music*/
    companion object {
        lateinit var musicListPA: ArrayList<Music>
        //музыкальная позиция
        var songPosition: Int = 0
        //проигрыватель
        var mediaPlayer: MediaPlayer? = null
        //плай-пауза
        var isPlaying: Boolean = false
        //сервис
        var musicService:MusicService? = null
         //TODO Глюки или утечка памяти? добавил анотацию
        //перенес в объект для доступа в NotificationReceiver
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        //повторение песни
        var repeat:Boolean = false
        //таймер
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPick)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //для запуска сервиса// (Intent) - это механизм для описания одной операции - выбрать фотографию, отправить письмо
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        initializeLayout()

        /**кнопки*/
        binding.previousBtnPA.setOnClickListener {
            prevNextSong(increment = false)
        }

        binding.nextBtnPA.setOnClickListener {
            prevNextSong(increment = true)
        }

        binding.playPauseBtnPA.setOnClickListener{
            if(isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        /** кнопка возврат*/
        //https://developer.alexanderklimov.ru/android/theory/activity_methods.php
        binding.backBtnPA.setOnClickListener {
            //C помощью метода finish() можно завершить работу активности.
            finish()
        }
        /**seekBar*/
        //https://developer.android.com/reference/android/widget/SeekBar
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
        /**repeat повторение песни*/
        binding.repeatBtnPA.setOnClickListener {
            if(!repeat){
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            }else{
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }
        /** эквалайзер*/
        //https://developer.android.com/reference/kotlin/android/media/audiofx/AudioEffect
        binding.equalizerBtnPA.setOnClickListener{
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent,13)
            }catch (e:Exception){
                Toast.makeText(this, R.string.equalizer_not_supported, Toast.LENGTH_SHORT).show()
            }
        }
        /** таймер*/
        binding.timerBtnPA.setOnClickListener{
            val timer = min15 || min30 || min60
            if(!timer) {
                showBottomSheetDialog()
            }else {
                //вариант с уведомлением(запросом) пользователя
                //Кроме стандартного диалогового окна AlertDialog можно использовать диалоговое
                // окно в стиле Material Design с помощью класса MaterialAlertDialogBuilder.
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(R.string.timer_stop)
                    .setMessage(R.string.question_stop_timer)
                    .setPositiveButton(R.string.yes){ _, _->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
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

    }
    /**для override fun onCreate(savedInstanceState: Bundle?) вынес в функцию, что бы не мешало */
    fun initializeLayout(){
        //получаю данные из class MusicAdapter
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MusicAdapter"->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                //плэй-пауза
                setLayout()
                //медиаплеер
                //createMediaPlayer() - вынес в сервис
            }
            "MainActivity" ->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                //случайный порядок
                musicListPA.shuffle()
                //плэй-пауза
                setLayout()
                //медиаплеер
                //createMediaPlayer() - вынес в сервис
            }
        }
    }

    /** подгрузка главной иконки*/
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPA[songPosition].title
        //повторение песни
        if (repeat){
            binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
        //смена цвета кнопки таймер
        if(min15 || min30 || min60 == true) {
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
    }

    /** медиаплеер*/
    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            //плэй-пауза смена кнопки
            isPlaying = true
            binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            //уведомления
            musicService!!.showNotification(R.drawable.pause_icon)
            //seekBar взял функцию из Music
            //binding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            //продолжение воспроизведения после окончания песни
            musicService!!.mediaPlayer!!.setOnCompletionListener (this)

        }catch (e:Exception){
            return
        }
    }

    /** плэй-пауза*/
    private fun playMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        //для меню-уведомления
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        //для меню-уведомления
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    /** кнопки вперед - назад*/
    private fun prevNextSong(increment: Boolean){
        if(increment){
            //позиция увеличена
            //++songPosition
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }else{
        //позиция уменьшена
        //--songPosition
        setSongPosition(increment = false)
        setLayout()
        createMediaPlayer()
        }
    }


    /**для class MusicService и Binder*/
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
       val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    /**продолжение воспроизведения после окончания песни*/
    //https://developer.android.com/reference/kotlin/android/media/MediaPlayer
    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (
            e:Exception
        ){
            return
        }
    }

    /**для эквалайзера*/
        //Если использую метод startActivityForResult(), то необходимо переопределить в коде метод для
    // приёма результата onActivityResult() и обработать полученный результат
    @Deprecated("Deprecated in Java")//анотация, если версия устарела
    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 || resultCode == RESULT_OK){
            return
        }
    }

    /**для таймера - отображения списка минут*/
    //https://developer.alexanderklimov.ru/android/dialogfragment_alertdialog.php
    private fun showBottomSheetDialog(){
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            //отправить сообщение
            Toast.makeText(baseContext, R.string.timer_stop_15, Toast.LENGTH_SHORT).show()
            //смена цвета кнопки
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min15 = true
            Thread{
                Thread.sleep(15 * 60000)
                if(min15 == true){
                exitApplication()
                }
            }.start()
            //когда задача активна, нукжно закрыть диалоговое окно
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext, R.string.timer_stop_30, Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min30 = true
            Thread{
                Thread.sleep(30 * 60000)
                if(min30 == true){
                    exitApplication()
                }
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext, R.string.timer_stop_60, Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min60 = true
            Thread{
                Thread.sleep(60 * 60000)
                if(min60 == true){
                    exitApplication()
                }
            }.start()
            dialog.dismiss()
        }
    }
}

//Для создания всплывающего уведомления необходимо инициализировать объект Toast при помощи метода
//Toast.makeText(), а затем вызвать метод show() для отображения сообщения на экране
//LENGTH_SHORT — (По умолчанию) показывает текстовое уведомление на короткий промежуток времени;
//LENGTH_LONG — показывает текстовое уведомление в течение длительного периода времени.
//https://developer.alexanderklimov.ru/android/toast.php