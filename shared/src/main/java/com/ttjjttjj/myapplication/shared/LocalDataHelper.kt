package com.ttjjttjj.myapplication.shared

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.ttjjttjj.mymediasession.shared.R


object LocalDataHelper {

    fun rawToUri(context: Context, id: Int): Uri {
        val uriStr = "android.resource://" + context.packageName + "/" + id
        return Uri.parse(uriStr)
    }

    fun getPlayList(): List<PlayBean> {
        val list: MutableList<PlayBean> = ArrayList()
        val playBean = PlayBean()
        playBean.mediaId = R.raw.fourgirl_xinyuan
        playBean.title = "四个女生"
        playBean.artist = "心愿"
        list.add(playBean)
        val playBean2 = PlayBean()
        playBean2.mediaId = R.raw.foxtail_grass
        playBean2.title = "foxtail_grass"
        playBean2.artist = "风舞"
        list.add(playBean2)
        val playBean3 = PlayBean()
        playBean3.mediaId = R.raw.two_tiger
        playBean3.title = "两只老虎"
        playBean3.artist = "群星"
        list.add(playBean3)
        val playBean4 = PlayBean()
        playBean4.mediaId = R.raw.caimogudexiaoguliang
        playBean4.title = "采蘑菇的小姑娘"
        playBean4.artist = "群星"
        list.add(playBean4)
        return list
    }

    fun transformPlayList(playBeanList: List<PlayBean?>?): ArrayList<MediaBrowserCompat.MediaItem> {
        //我们模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.fourgirl_xinyuan)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "四个女生")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "心愿")
            .build()
        val metadata2 = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.foxtail_grass)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "foxtail_grass")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "风舞")
            .build()
        val metadata3 = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.two_tiger)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "两只老虎")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "群星")
            .build()
        val metadata4 = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.caimogudexiaoguliang)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "采蘑菇的小姑娘")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "群星")
            .build()
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        mediaItems.add(createMediaItem(metadata))
        mediaItems.add(createMediaItem(metadata2))
        mediaItems.add(createMediaItem(metadata3))
        mediaItems.add(createMediaItem(metadata4))
        return mediaItems
    }

    fun transformPlayBean(bean: PlayBean): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + bean.mediaId)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, bean.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, bean.artist)
            .build()
    }

    fun transformPlayBeanByDuration(bean: PlayBean, duration: Long): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + bean.mediaId)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, bean.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, bean.artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            .build()
    }


    private fun createMediaItem(metadata: MediaMetadataCompat): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(
            metadata.description,
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
    }
}