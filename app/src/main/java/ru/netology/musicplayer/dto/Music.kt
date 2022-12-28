package ru.netology.musicplayer.dto

import android.media.MediaMetadataRetriever
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
fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds =
        (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) - minutes * TimeUnit.SECONDS
            .convert(1, TimeUnit.MINUTES))
    //TODO проверить отображение!!!
    return String.format("%02d:%02d", minutes, seconds)
}

/**для закгрузки изображения в меню-уведомление. сделает фон уведомлению от песни*/
fun getImgArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)

    return retriever.embeddedPicture
}
//Класс MediaMetadataRetriever предоставляет унифицированный интерфейс для извлечения кадров и
// метаданных из входного медиафайла. Он находится в пакете android.media . Например: получение
// названия песни, имени исполнителя, ширины или высоты видео, формата видео ,
// продолжительности медиа, даты изменения медиа и т
//https://progler.ru/blog/klass-mediametadataretriever-v-android-s-primerami

