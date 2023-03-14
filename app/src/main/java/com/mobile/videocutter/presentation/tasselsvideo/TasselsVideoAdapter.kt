package com.mobile.videocutter.presentation.tasselsvideo

import androidx.databinding.ViewDataBinding
import com.mobile.videocutter.R
import com.mobile.videocutter.base.common.adapter.BaseAdapter
import com.mobile.videocutter.base.common.adapter.BaseVH
import com.mobile.videocutter.base.extension.getAppDrawable
import com.mobile.videocutter.databinding.ToolItemBinding
import com.mobile.videocutter.domain.model.ToolVideo

class TasselsVideoAdapter : BaseAdapter() {

    override fun getLayoutResource(viewType: Int) = R.layout.tool_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return TasselsVideoVH(binding as ToolItemBinding)
    }

    class TasselsVideoVH(val binding: ToolItemBinding) : BaseVH<ToolVideo>(binding) {
        override fun onBind(data: ToolVideo) {
            super.onBind(data)
            binding.ivToolItmAvatar.background = getAppDrawable(data.image)
            binding.tvToolItmName.text = data.name
        }
    }
}
