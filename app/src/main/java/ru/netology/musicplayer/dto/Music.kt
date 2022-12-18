package ru.netology.musicplayer.dto

import java.util.concurrent.TimeUnit

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    var duration: Long = 0,//продолжительность
    val path: String,
    val artUri: String// Glide загрузка изображений иконки
)

/**для продолжительности. Нужно форматирование. Иначе отобр куча цифр*/
fun formatDuration(duration: Long): String{
   val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)- minutes*TimeUnit.SECONDS
        .convert(1, TimeUnit.MINUTES))
    //TODO проверить отображение!!!
    return String.format("%02d:%02d", minutes, seconds)
}