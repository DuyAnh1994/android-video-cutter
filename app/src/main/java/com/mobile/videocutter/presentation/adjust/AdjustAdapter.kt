package com.mobile.videocutter.presentation.adjust

import androidx.databinding.ViewDataBinding
import com.mobile.videocutter.R
import com.mobile.videocutter.base.common.adapter.BaseAdapter
import com.mobile.videocutter.base.common.adapter.BaseVH
import com.mobile.videocutter.base.extension.setOnSafeClick
import com.mobile.videocutter.databinding.AdjustVideoItemBinding
import com.mobile.videocutter.domain.model.LocalVideo
import loadImage

class AdjustAdapter : BaseAdapter() {

    companion object {
        private const val DATA_ADJUST_VIDEO_ITEM = 0
        private const val ADD_DATA_ADJUST_VIDEO_ITEM = 1
    }

    var listener: IListener? = null

    override fun getLayoutResource(viewType: Int): Int {
        return when (viewType) {
            DATA_ADJUST_VIDEO_ITEM -> R.layout.adjust_video_item
            ADD_DATA_ADJUST_VIDEO_ITEM -> R.layout.add_video_item
            else -> R.layout.adjust_video_item
        }
    }

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return when (viewType) {
            DATA_ADJUST_VIDEO_ITEM -> AdjustVH(binding)
            ADD_DATA_ADJUST_VIDEO_ITEM -> AddAdjustVH(binding)
            else -> AdjustVH(binding)
        }
    }

    override fun getItemViewTypeCustom(position: Int): Int {
        return when (dataList[position]) {
            is LocalVideo -> DATA_ADJUST_VIDEO_ITEM
            else -> ADD_DATA_ADJUST_VIDEO_ITEM
        }
    }

    inner class AddAdjustVH(private val binding: ViewDataBinding) : BaseVH<Any>(binding)

    inner class AdjustVH(private val binding: ViewDataBinding) : BaseVH<LocalVideo>(binding) {

        val viewBinding = binding as AdjustVideoItemBinding

        init {
            if (dataList.size > 1) {
                val item = getDataAtPosition(0) as? LocalVideo
                if (item != null) {
                    listener?.onLoadLocalVideoDefault(item)
                }
            }
            viewBinding.ivAdjustDeleteItm.setOnSafeClick {
                val item = getDataAtPosition(bindingAdapterPosition) as? LocalVideo
                if (item != null) {
                    listener?.onDelete(item)
                }
            }

            viewBinding.root.setOnSafeClick {
                val item = getDataAtPosition(bindingAdapterPosition) as? LocalVideo
                if (item != null) {
                    listener?.onClick(item)
                }
            }
        }

        override fun onBind(data: LocalVideo) {
            super.onBind(data)

            viewBinding.ivAdjustVideoItmImage.loadImage(data.getImageThumbPath())
        }
    }

    interface IListener {
        fun onDelete(localVideo: LocalVideo)
        fun onClick(localVideo: LocalVideo)
        fun onLoadLocalVideoDefault(localVideo: LocalVideo)
    }
}
