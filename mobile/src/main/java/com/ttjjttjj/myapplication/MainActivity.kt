package com.ttjjttjj.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ttjjttjj.myapplication.databinding.ActivityMainBinding
import com.ttjjttjj.mybaselib.base.activity.BAViewBindingByReflect
import com.ttjjttjj.mybaselib.ext.extensions.logd
import com.ttjjttjj.mybaselib.ext.extensions.loge
import com.ttjjttjj.mybaselib.ext.util.DialogUtil
import com.ttjjttjj.mymediasession.shared.MyMusicService


/**
 * 音乐播放页面
 *
 * @author tj
 */
class MainActivity : BAViewBindingByReflect<ActivityMainBinding, MainViewModel>(), View.OnClickListener{
    private var mHandler: Handler? = null
    private var isSetMax = false
    private var mAllSize = 0
    private lateinit var mDialog : AppCompatDialog
    private lateinit var mAdapter : MyAdapter
    private var mSelectedPosition = 0

    override fun initView() {
        viewBinding.btnPlayPause.setOnClickListener(this)
        viewBinding.btnPre.setOnClickListener(this)
        viewBinding.btnNext.setOnClickListener(this)
        viewBinding.imageBtn.setOnClickListener(this)
    }

    override fun initData() {
        mHandler = Handler(mainLooper)
        startService(Intent(this, MyMusicService::class.java))
        viewModel.createMediaBrowser(this)
        viewModel.setMediaControlCompatCallBack(mMediaControllerCallback)
        thread.start()
    }

    override fun initObserve() {

        viewModel.mListData.observe(this, {
            for (item in it) {
                "observe - ${item.description.title.toString()}".logd()
            }
            mAllSize = it.size
            createDialog(it.size)
        })

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnPlayPause -> {
                handlerPlayEvent()
            }
            R.id.imageBtn -> {
                mDialog.show()
            }
            R.id.btn_pre -> {
                viewModel.mMediaControllerCompat.transportControls.skipToPrevious()
                mSelectedPosition--
                "-- $mSelectedPosition".logd()
                if (mSelectedPosition < 0) {
                    mSelectedPosition = mAllSize - 1
                }
                mAdapter.setSelected(mSelectedPosition)
                mAdapter.notifyDataSetChanged()
                loadCover(mSelectedPosition)
            }
            R.id.btn_next -> {
                viewModel.mMediaControllerCompat.transportControls.skipToNext()
                mSelectedPosition++
                "++ $mSelectedPosition".logd()
                if (mSelectedPosition > (mAllSize -1)) {
                    mSelectedPosition = 0
                }
                mAdapter.setSelected(mSelectedPosition)
                mAdapter.notifyDataSetChanged()
                loadCover(mSelectedPosition)
            }
        }
    }

    /**
     * 选择列表数据去播放
     * @param position
     * @param bundle
     */
    private fun setMediaControllerCompat(position: Int) {
        val bundle = Bundle()
        bundle.putInt("playPosition", position)
        viewModel.mMediaControllerCompat.transportControls.playFromUri(
            rawToUri(Integer.valueOf(viewModel.mListData.value?.get(position)?.mediaId)),
            bundle
        )
    }

    private fun rawToUri(id: Int): Uri? {
        val uriStr = "android.resource://$packageName/$id"
        return Uri.parse(uriStr)
    }

    /**
     * MediaControllerCompat 操作UI按钮，执行pause, play等
     */
    private fun handlerPlayEvent() {
        when (viewModel.mMediaControllerCompat.playbackState.state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                "onClick pause".logd()
                viewModel.mMediaControllerCompat.transportControls.pause()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                "onClick play".logd()
                viewModel.mMediaControllerCompat.transportControls.play()
            }
            else -> {
                "onClick other".logd()
                viewModel.mMediaControllerCompat.transportControls.playFromSearch("", null)
            }
        }
    }

    /**
     * MediaControlCompat 的回调
     */
    private val mMediaControllerCallback = object : MediaControllerCompat.Callback() {

        /**
         * 状态变化
         */
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            "onPlaybackStateChanged --->".logd()
            updatePlayState(state)
        }

        /**
         * 数据变化
         */
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            "onMetadataChanged --->".logd()
            updatePlayMetadata(metadata)
        }
    }

    private fun updatePlayState(state: PlaybackStateCompat?) {
        if (state == null) {
            return
        }
        when (state.state) {
            PlaybackStateCompat.STATE_NONE -> {
                "STATE_NONE".logd()
                viewBinding.btnPlayPause.setImageResource(R.mipmap.ic_play)
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                "STATE_PAUSED".logd()
                viewBinding.btnPlayPause.setImageResource(R.mipmap.ic_play)
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                "STATE_PLAYING".logd()
                viewBinding.btnPlayPause.setImageResource(R.mipmap.ic_pause)
            }
        }
    }

    private fun updatePlayMetadata(metadata: MediaMetadataCompat?) {
        if (metadata == null) {
            return
        }
        viewBinding.tvTitle.text = (metadata.description.title.toString() + " - " + metadata.description.subtitle)
        val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
        if (duration > 1) {
            viewBinding.seekBar.max = duration.toInt()
            viewBinding.tvTextDuration.text = setDurationTime(duration)
            isSetMax = true
            "ui duration : $duration".loge()
        }
    }

    /**
     * 更新音乐的进度
     */
    private val isWork = true
    private var thread: Thread = object : Thread() {
        override fun run() {
            super.run()
            "thread isWork: $isWork".logd()
            while (isWork) {
                try {
                    try {
                        sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    if (null == viewModel.mMediaControllerCompat) {
                        "null == mMediaControllerCompat".logd()
                        continue
                    }
                    val position = viewModel.mMediaControllerCompat.playbackState.position
                    mHandler?.post {
                        if (isSetMax) {
                            viewBinding.seekBar.progress = position.toInt()
                            viewBinding.tvTextProgress.text = setProgressTime(position)
                            "setProgress: $position".logd()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 秒转化为00:00形式
     * @param curPosition
     */
    private fun setProgressTime(curPosition: Long): String {
        val cm = curPosition / 1000 / 60
        val cs = curPosition / 1000 % 60
        val builder = StringBuilder()
        return builder.append(cm / 10).append(cm % 10).append(":")
            .append(cs / 10).append(cs % 10).toString()
    }

    private fun setDurationTime(maxLen: Long): String {
        val mm = maxLen / 1000 / 60
        val ms = maxLen / 1000 % 60
        val builder = StringBuilder()
        return builder.append(mm / 10).append(mm % 10).append(":")
            .append(ms / 10).append(ms % 10).toString()
    }


    private fun createDialog(size:Int) {
        mDialog = AppCompatDialog(this)
        mDialog.setTitle("PlayList($size)")
        var layoutView = LayoutInflater.from(this).inflate(R.layout.dialog_playlist, null)
        mDialog.setContentView(layoutView)
        val recyclerView = layoutView.findViewById<RecyclerView>(R.id.rvPlaylist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = MyAdapter(viewModel.mListData.value as MutableList<MediaBrowserCompat.MediaItem>)
        recyclerView.adapter = mAdapter
        /**
         * 音乐播放列表，选择
         */
        mAdapter.setOnItemClickListener { _, _, position ->
            mSelectedPosition = position
            setMediaControllerCompat(position)
            mAdapter.setSelected(position)
            mAdapter.notifyDataSetChanged()
            mDialog.dismiss()

            loadCover(position)
        }

        DialogUtil.setWith(mDialog, WindowManager.LayoutParams.MATCH_PARENT)
        DialogUtil.setGravity(mDialog, Gravity.BOTTOM)
        DialogUtil.setBackgroundDrawableResource(mDialog, R.drawable.bg_bottom_dialog)
        DialogUtil.setAnimations(mDialog, R.style.BottomDialogTransition)
        mDialog.setCanceledOnTouchOutside(true)
    }


    /**
     * 加载音乐封面
     */
    private fun loadCover(position: Int) {
        val uriStr = rawToUri(Integer.valueOf(viewModel.mListData.value?.get(position)?.mediaId))
        if (uriStr != null) {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(mContext, uriStr)
            val cover = mediaMetadataRetriever.embeddedPicture
            val bitmap = BitmapFactory.decodeByteArray(cover, 0, cover?.size!!)
            viewBinding.ivAlbumIcon.setImageBitmap(bitmap)
        }
    }

}