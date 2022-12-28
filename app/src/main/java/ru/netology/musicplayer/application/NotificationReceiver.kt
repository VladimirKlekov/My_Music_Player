package ru.netology.musicplayer.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    //BroadcastReceiver реализует onReceive(Context, Intent)где метод onReceive каждое сообщение
    // принимает как параметр объекта Intent.
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> Toast.makeText(context,"Previous clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.PLAY -> Toast.makeText(context,"Play clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.NEXT -> Toast.makeText(context,"Next clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.EXIT -> exitProcess(1)
        }
    }
}

//P.S.Приложения могут получать Android BroadcastReceiver двумя способами: через приемники,
//объявленные в манифесте, и приемники, зарегистрированные в контексте.
//https://tutorial.eyehunts.com/android/android-broadcastreceiver-example-kotlin/