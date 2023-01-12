package ru.netology.musicplayer.dto

import android.media.MediaMetadataRetriever
import ru.netology.musicplayer.FavouriteActivity
import ru.netology.musicplayer.PlayerActivity
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    var duration: Long = 0,//продолжительность
    val path: String,
    val artUri: String// Glide загрузка изображений иконки
)
class Playlist{
    lateinit var name: String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createdOn: String
}
class MusicPlaylist{
    var ref: ArrayList<Playlist> = ArrayList()
}

/**для продолжительности. Нужно форматирование. Иначе отображается куча цифр*/
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

//TODO
fun setSongPosition(increment: Boolean) {
    if (!PlayerActivity.repeat)//для повторения песни
    {
        if (increment) {
            if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition) {
                PlayerActivity.songPosition = 0
            } else ++PlayerActivity.songPosition
        } else {
            if (0 == PlayerActivity.songPosition) {
                PlayerActivity.songPosition = PlayerActivity.musicListPA.size - 1
            } else --PlayerActivity.songPosition
        }
    }

}
/**для добавления favourite*/
fun favouriteChecker(id: String): Int {
    PlayerActivity.isFavourite = false
    FavouriteActivity.favouriteSong.forEachIndexed { index, music ->
        if (id == music.id) {
            PlayerActivity.isFavourite = true
            return index
        }
    }
    //return 1
    return -1
}

/**сделал отдельную функцию для выхода из приложения*/
fun exitApplication() {
    //добавил условие. иначе глючит при выходе
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null
        exitProcess(1)
    }

}
//Класс MediaMetadataRetriever предоставляет унифицированный интерфейс для извлечения кадров и
// метаданных из входного медиафайла. Он находится в пакете android.media . Например: получение
// названия песни, имени исполнителя, ширины или высоты видео, формата видео ,
// продолжительности медиа, даты изменения медиа и т
//https://progler.ru/blog/klass-mediametadataretriever-v-android-s-primerami

