package com.ttjjttjj.myapplication

import android.annotation.SuppressLint
import android.support.v4.media.MediaBrowserCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ttjjttjj.mybaselib.ext.extensions.loge

class MyAdapter(data: MutableList<MediaBrowserCompat.MediaItem>)
    : BaseQuickAdapter<MediaBrowserCompat.MediaItem, BaseViewHolder>(R.layout.item_paly, data) {

    private var mSelectedId = 0

    @SuppressLint("ResourceAsColor")
    override fun convert(holder: BaseViewHolder, item: MediaBrowserCompat.MediaItem) {
        holder.setText(R.id.tvPosition, "${holder.adapterPosition}")
        holder.setText(R.id.tvTitle, data[holder.adapterPosition].description.title.toString())
        holder.setText(R.id.tvArtist, " - ${data[holder.adapterPosition].description.subtitle.toString()}")

        "选中$mSelectedId".loge()
        if (holder.adapterPosition == mSelectedId) {
            holder.setTextColor(R.id.tvPosition, R.color.colorPrimary)
            holder.setTextColor(R.id.tvTitle, R.color.colorPrimary)
            holder.setTextColor(R.id.tvArtist, R.color.colorPrimary)
            holder.setVisible(R.id.mark, true)
        } else {
            holder.setTextColor(R.id.tvPosition, R.color.black)
            holder.setTextColor(R.id.tvTitle, R.color.black)
            holder.setTextColor(R.id.tvArtist, R.color.black)
            holder.setVisible(R.id.mark, false)
        }
    }

    fun setSelected(selectedId: Int) {
        this.mSelectedId = selectedId
    }


}