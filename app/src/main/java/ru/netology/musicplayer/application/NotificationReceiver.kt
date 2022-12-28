package ru.netology.musicplayer.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import ru.netology.musicplayer.PlayerActivity
import ru.netology.musicplayer.R
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    //BroadcastReceiver реализует onReceive(Context, Intent)где метод onReceive каждое сообщение
    // принимает как параметр объекта Intent.
    override fun onReceive(context: Context?, intent: Intent?) {

        //уведомления при нажатии
        when(intent?.action){
            ApplicationClass.PREVIOUS -> Toast.makeText(context,"Previous clicked", Toast.LENGTH_SHORT).show()
            //ApplicationClass.PLAY -> Toast.makeText(context,"Play clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.PLAY ->
                if(PlayerActivity.isPlaying) {
                    pauseMusic()
                } else{
                    playMusic()
                }

            ApplicationClass.NEXT -> Toast.makeText(context,"Next clicked", Toast.LENGTH_SHORT).show()
            //ApplicationClass.EXIT -> exitProcess(1)
            ApplicationClass.EXIT ->{
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    //действия для кнопок в меню-уведомлении
    private fun playMusic(){
        PlayerActivity.isPlaying =true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
    }
}

//P.S.Приложения могут получать Android BroadcastReceiver двумя способами: через приемники,
//объявленные в манифесте, и приемники, зарегистрированные в контексте.
//https://tutorial.eyehunts.com/android/android-broadcastreceiver-example-kotlin/

//службы foreground
//https://developer.android.com/guide/components/foreground-services