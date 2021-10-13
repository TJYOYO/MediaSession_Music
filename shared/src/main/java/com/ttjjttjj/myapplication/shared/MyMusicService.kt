package com.ttjjttjj.mymediasession.shared

import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.ttjjttjj.myapplication.shared.LocalDataHelper
import com.ttjjttjj.myapplication.shared.PlayBean
import com.ttjjttjj.mybaselib.ext.extensions.logd
import com.ttjjttjj.mybaselib.ext.extensions.loge
import java.io.IOException
import java.util.*

/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app"s UI, which gives a seamless playback
 * experience to the user.
 *
 *
 * To implement a MediaBrowserService, you need to:
 *
 *  *  Extend [MediaBrowserServiceCompat], implementing the media browsing
 * related methods [MediaBrowserServiceCompat.onGetRoot] and
 * [MediaBrowserServiceCompat.onLoadChildren];
 *
 *  *  In onCreate, start a new [MediaSessionCompat] and notify its parent
 * with the session"s token [MediaBrowserServiceCompat.setSessionToken];
 *
 *  *  Set a callback on the [MediaSessionCompat.setCallback].
 * The callback will receive all the user"s actions, like play, pause, etc;
 *
 *  *  Handle all the actual music playing using any method your app prefers (for example,
 * [android.media.MediaPlayer])
 *
 *  *  Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 * [MediaSessionCompat.setPlaybackState]
 * [MediaSessionCompat.setMetadata] and
 * [MediaSessionCompat.setQueue])
 *
 *  *  Declare and export the service in AndroidManifest with an intent receiver for the action
 * android.media.browse.MediaBrowserService
 *
 * To make your app compatible with Android Auto, you also need to:
 *
 *  *  Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 * with a &lt;automotiveApp&gt; root element. For a media app, this must include
 * an &lt;uses name="media"/&gt; element as a child.
 * For example, in AndroidManifest.xml:
 * &lt;meta-data android:name="com.google.android.gms.car.application"
 * android:resource="@xml/automotive_app_desc"/&gt;
 * And in res/values/automotive_app_desc.xml:
 * &lt;automotiveApp&gt;
 * &lt;uses name="media"/&gt;
 * &lt;/automotiveApp&gt;
 *
 */
class MyMusicService : MediaBrowserServiceCompat() {

    private lateinit var mSession: MediaSessionCompat
    private lateinit var mAudioManager: AudioManager
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mPlaybackStateCompat: PlaybackStateCompat
    private var position = -1
    private val mPlayBeanList: List<PlayBean> = LocalDataHelper.getPlayList()
    private var isHaveAudioFocus = false

    override fun onCreate() {
        super.onCreate()
        createMediaSessionCompat()
        createPlayer()
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    /**
     * 创建 MediaSessionCompat
     */
    private fun createMediaSessionCompat() {

        mSession = MediaSessionCompat(this, "MyMusicService")

        // 设置token后会触发MediaBrowserCompat.ConnectionCallback的回调方法
        // 表示MediaBrowser与MediaBrowserService连接成功
        sessionToken = mSession.sessionToken

        // 回调
        mSession.setCallback(mSessionCallback)
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        // 封装了各种播放状态
        mPlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
            .setActions(getAvailableActions(PlaybackStateCompat.STATE_NONE))
            .build()
        mSession.setPlaybackState(mPlaybackStateCompat)
        mSession.isActive = true
    }

    /**
     * MediaBrowserCompat请求连接触发，返回rootId
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): MediaBrowserServiceCompat.BrowserRoot? {
        "onGetRoot-----------".loge()
        return BrowserRoot("root", null)
    }

    /**
     * MediaBrowserCompat 调用订阅方法，触发 service的 onLoadChildren
     *
     * onLoadChildren 中会去获取数据（可以是异步的）
     * @param parentId
     * @param result
     */
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        "onLoadChildren--------".loge()

        // 将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach()
        /**
         * 模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
         */
        val mediaItems: ArrayList<MediaItem> = LocalDataHelper.transformPlayList(mPlayBeanList)

        // 向MediaBrowserCompat发送 播放列表数据
        result.sendResult(mediaItems)
    }

    /**
     * 创建播放器 Player
     */
    private fun createPlayer() {
        mMediaPlayer = MediaPlayer()
        mMediaPlayer.setOnPreparedListener(mPreparedListener)
        mMediaPlayer.setOnCompletionListener(mCompletionListener)
    }

    /**
     * 响应MediaControllerCompat 指令的回调
     *
     */
    private val mSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            /**
             * 响应MediaController.getTransportControls().play
             */
            override fun onPlay() {
                "onPlay".logd()
                handlePlay()
            }

            /**
             * 响应MediaController.getTransportControls().onPause
             */
            override fun onPause() {
                "onPause".logd()
                handlePause(true)
            }

            override fun onSkipToPrevious() {
                "onSkipToPrevious".logd()
                val pos: Int = (position + mPlayBeanList.size - 1) % mPlayBeanList.size
                handlePlayPosition(pos)
            }

            override fun onSkipToNext() {
                "onSkipToNext".logd()
                val pos: Int = (position + 1) % mPlayBeanList.size
                handlePlayPosition(pos)
            }

            /**
             * 响应MediaController.getTransportControls().playFromUri
             *
             * @param uri
             * @param extras
             */
            override fun onPlayFromUri(uri: Uri, extras: Bundle) {
                "onPlayFromUri".logd()
                val position = extras.getInt("playPosition")
                setPlayPosition(position)
                handlePlayUri(uri)
            }

            override fun onPlayFromSearch(query: String, extras: Bundle) {}
            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                // 对物理按键的支持
                "MediaSessionCallback——》onMediaButtonEvent $mediaButtonEvent".logd()
                return super.onMediaButtonEvent(mediaButtonEvent)
            }

            override fun onSeekTo(pos: Long) {
                "onSeekTo ---> pos = $pos".logd()
                mMediaPlayer.seekTo(pos.toInt())
                mPlaybackStateCompat = PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING,
                        mMediaPlayer.currentPosition.toLong(),
                        1.0f)
                    .setActions(getAvailableActions(PlaybackStateCompat.STATE_PLAYING))
                    .build()
                mSession.setPlaybackState(mPlaybackStateCompat)
            }
        }

    /**
     * 判断播放状态是否暂停，以及获取音频焦点，然后去操作播放器player
     *
     * PlaybackStateCompat.STATE_PLAYING
     */
    private fun handlePlay() {
        if (mPlaybackStateCompat.state == PlaybackStateCompat.STATE_PAUSED
            && requestAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        ) {
            mMediaPlayer.start()
            mPlaybackStateCompat = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,
                    mMediaPlayer.currentPosition.toLong(),
                    1.0f)
                .setActions(getAvailableActions(PlaybackStateCompat.STATE_PLAYING))
                .build()
            mSession.setPlaybackState(mPlaybackStateCompat)

            // 更新视频的总进度, setMetadata 会更新MediaControlCompat的onMetadataChanged
            mSession.setMetadata(LocalDataHelper.transformPlayBeanByDuration(getPlayBean(),
                mMediaPlayer.duration.toLong()))
            "mMediaPlayer.getDuration()=${mMediaPlayer.duration}".logd()
        }
    }

    /**
     * PlaybackStateCompat.STATE_PAUSED
     * @param isAbandonFocus
     */
    private fun handlePause(isAbandonFocus: Boolean) {
        if (mMediaPlayer == null) {
            return
        }
        if (mPlaybackStateCompat.state == PlaybackStateCompat.STATE_PLAYING) {
            mMediaPlayer.pause()
            mPlaybackStateCompat = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED,
                    mMediaPlayer.currentPosition.toLong(),
                    1.0f)
                .setActions(getAvailableActions(PlaybackStateCompat.STATE_PAUSED))
                .build()
            mSession.setPlaybackState(mPlaybackStateCompat)
        }
        if (isAbandonFocus) {
        }
    }

    private fun handlePlayPosition(pos: Int) {
        val playBean = setPlayPosition(pos) ?: return
        handlePlayUri(LocalDataHelper.rawToUri(this, playBean.mediaId))
    }

    private fun handlePlayUri(uri: Uri?) {
        if (uri == null) {
            return
        }
        if (requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return
        }
        mMediaPlayer.reset()
        mMediaPlayer.isLooping = true
        try {
            mMediaPlayer.setDataSource(this@MyMusicService, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer.prepareAsync()
        mPlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_CONNECTING,
                mMediaPlayer.currentPosition.toLong(),
                1.0f)
            .setActions(getAvailableActions(PlaybackStateCompat.STATE_CONNECTING))
            .build()
        mSession.setPlaybackState(mPlaybackStateCompat)
        //我们可以保存当前播放音乐的信息，以便客户端刷新UI
        Handler(Looper.getMainLooper()).postDelayed({
            mSession.setMetadata(LocalDataHelper.transformPlayBeanByDuration(getPlayBean(),
                mMediaPlayer.duration.toLong()))
        }, 1000)
    }

    private fun getPlayBean(): PlayBean {
        return mPlayBeanList[position]
    }

    private fun setPlayPosition(pos: Int): PlayBean? {
        if (pos >= 0 && pos < mPlayBeanList.size) {
            position = pos
            return mPlayBeanList[position]
        }
        return null
    }

    /**
     * 监听MediaPlayer.prepare()
     */
    private val mPreparedListener = OnPreparedListener {
        mMediaPlayer.start()
        mPlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING,
                mMediaPlayer.currentPosition.toLong(),
                1.0f)
            .setActions(getAvailableActions(PlaybackStateCompat.STATE_PLAYING))
            .build()
        mSession.setPlaybackState(mPlaybackStateCompat)
    }

    /**
     * 监听播放结束的事件
     */
    private val mCompletionListener = OnCompletionListener {
        mPlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, mMediaPlayer.currentPosition.toLong(), 1.0f)
            .setActions(getAvailableActions(PlaybackStateCompat.STATE_NONE))
            .build()
        mSession.setPlaybackState(mPlaybackStateCompat)
        mMediaPlayer.reset()
    }

    fun getAvailableActions(@PlaybackStateCompat.State state: Int): Long {
        var actions = (PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_REWIND
                or PlaybackStateCompat.ACTION_FAST_FORWARD)
        actions = if (state == PlaybackStateCompat.STATE_PLAYING) {
            actions or PlaybackStateCompat.ACTION_PAUSE
        } else {
            actions or PlaybackStateCompat.ACTION_PLAY
        }
        return actions
    }

    /**
     * 请求音频焦点
     * @return
     */
    private fun requestAudioFocus(): Int {
        val result =
            mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
        isHaveAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result
        if (isHaveAudioFocus) {
            //mAudioManager.registerMediaButtonEventReceiver(mMediaButtonReceive)
        }
        "requestAudioFocus $isHaveAudioFocus".logd()
        return result
    }

    private fun abandonAudioFocus() {
        val result = mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
        isHaveAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result
    }

    private var mOnAudioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            "onAudioFocusChange  focusChange=$focusChange, before isHaveAudioFocus=$isHaveAudioFocus".logd()
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    isHaveAudioFocus = false
                    mSessionCallback.onPause()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    isHaveAudioFocus = false
                    " AUDIOFOCUS_LOSS_TRANSIENT ".logd()
                    handlePause(false)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    isHaveAudioFocus = true
                    mSessionCallback.onPlay()
                }
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                }
                else -> {
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        "onDestroy --->".loge()
        if (mMediaPlayer != null) {
            mMediaPlayer.release()
        }
        if (mSession != null) {
            mSession.release()
        }
    }


}