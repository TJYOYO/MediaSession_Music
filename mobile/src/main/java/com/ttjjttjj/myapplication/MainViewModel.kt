package com.ttjjttjj.myapplication

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ttjjttjj.mybaselib.ext.extensions.logd
import com.ttjjttjj.mybaselib.ext.extensions.loge
import com.ttjjttjj.mymediasession.shared.MyMusicService

class MainViewModel : ViewModel() {

    private lateinit var mMediaBrowserCompat: MediaBrowserCompat
    lateinit var mMediaControllerCompat: MediaControllerCompat
    private lateinit var mMediaId: String
    private var _listData = MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>>()
    val mListData : LiveData<MutableList<MediaBrowserCompat.MediaItem>> = _listData
    private lateinit var mContext : Context

    fun createMediaBrowser(context: Context) {
        this.mContext = context
        createMediaBrowserConnect()
    }

    /**
     * 创建 MediaBrowserCompat
     *
     * callback - connected -> subscribe -callback onChildrenLoaded
     */
    private fun createMediaBrowserConnect() {
        mMediaBrowserCompat = MediaBrowserCompat(mContext,
            ComponentName(mContext, MyMusicService::class.java),  // 绑定浏览器服务
            mBrowserCompatCallBack,  // 设置连接回调
            null)
        mMediaBrowserCompat.connect()
    }

    /**
     * MediaBrowserCompat 的连接回调
     */
    private var mBrowserCompatCallBack = object : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            super.onConnected()
            "onConnected------".loge()
            if (mMediaBrowserCompat.isConnected) {

                // 若Service允许客户端连接，则返回结果不为null, 若拒绝连接，则返回null
                mMediaId = mMediaBrowserCompat.root
                //Browser通过订阅的方式向Service请求数据
                mMediaBrowserCompat.unsubscribe(mMediaId)
                mMediaBrowserCompat.subscribe(mMediaId, mBrowserSubscriptionCallback)

                /**
                 * 连接上后，创建control
                 */
                createMediaControllerCompat()
            }
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            "onConnected failed------".loge()
        }
    }

    /**
     * MediaBrowserCompat 的订阅回调
     */
    private var mBrowserSubscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>,
        ) {
            super.onChildrenLoaded(parentId, children)
            //children 即为Service发送回来的媒体数据集合
            ("onChildrenLoaded------$children").logd()
            _listData.postValue(children)
        }
    }

    /**
     * 创建 MediaControlCompat
     */
    fun createMediaControllerCompat(){
        mMediaControllerCompat = MediaControllerCompat(mContext, mMediaBrowserCompat.sessionToken)
        mMediaControllerCompat.registerCallback(mMediaControllerCallback)
    }

    private lateinit var mMediaControllerCallback : MediaControllerCompat.Callback
    fun setMediaControlCompatCallBack(callback: MediaControllerCompat.Callback) {
        this.mMediaControllerCallback = callback
    }

    /**
     * 选择列表数据去播放
     * @param position
     * @param bundle
     */
    fun setMediaControllerCompat(position: Int) {
        val bundle = Bundle()
        bundle.putInt("playPosition", position)
        mMediaControllerCompat.transportControls.playFromUri(
            rawToUri(Integer.valueOf(mListData.value?.get(position)?.mediaId)),
            bundle
        )
    }

    private fun rawToUri(id: Int): Uri? {
        val uriStr = "android.resource://${mContext.packageName}/$id"
        return Uri.parse(uriStr)
    }

    /**
     * 加载音乐封面
     */
    fun loadCover(position: Int) : Bitmap? {
        val uriStr = rawToUri(Integer.valueOf(mListData.value?.get(position)?.mediaId))
        if (uriStr != null) {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(mContext, uriStr)
            val cover = mediaMetadataRetriever.embeddedPicture
            return BitmapFactory.decodeByteArray(cover, 0, cover?.size!!)
        }
        return null
    }
}