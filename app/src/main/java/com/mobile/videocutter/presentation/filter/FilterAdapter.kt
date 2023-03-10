package com.mobile.videocutter.presentation.filter

import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding
import com.mobile.videocutter.R
import com.mobile.videocutter.base.common.adapter.BaseAdapter
import com.mobile.videocutter.base.common.adapter.BaseVH
import com.mobile.videocutter.base.extension.*
import com.mobile.videocutter.databinding.FilterItemBinding
import com.mobile.videocutter.domain.model.FILTER_TYPE

class FilterAdapter : BaseAdapter() {
    companion object {
        private const val CHANGE_STATE_FILTER_PAYLOAD = "CHANGE_STATE_FILTER_PAYLOAD"
    }

    override fun getLayoutResource(viewType: Int) = R.layout.filter_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return FilterVH(binding)
    }

    private fun select(newPosition: Int) {
        val oldDataPosition = dataList.indexOfFirst {
            (it as FilterDisplay).isSelect
        }
        if (newPosition != oldDataPosition) {
            (dataList[newPosition] as FilterDisplay).isSelect = true
            notifyItemChanged(newPosition, CHANGE_STATE_FILTER_PAYLOAD)
            (dataList[oldDataPosition] as FilterDisplay).isSelect = false
            notifyItemChanged(oldDataPosition, CHANGE_STATE_FILTER_PAYLOAD)
        }
    }

    inner class FilterVH(private val binding: ViewDataBinding) : BaseVH<FilterDisplay>(binding) {
        private val viewBinding = binding as FilterItemBinding

        init {
            viewBinding.root.setOnSafeClick {
                val item = getDataAtPosition(bindingAdapterPosition) as? FilterDisplay
                if (item != null) {
                    select(bindingAdapterPosition)
                }
            }
        }

        override fun onBind(data: FilterDisplay) {
            super.onBind(data)

            viewBinding.ivFilterItm.setImageDrawable(data.getImage())
            viewBinding.tvFilterItm.text = data.getTitle()

            setStateData(data)
        }

        override fun onBind(data: FilterDisplay, payloads: List<Any>) {
            super.onBind(data, payloads)
            payloads.forEach {
                when (it) {
                    CHANGE_STATE_FILTER_PAYLOAD -> {
                        setStateData(data)
                    }
                }
            }
        }

        private fun setStateData(data: FilterDisplay) {
            if (data.isSelect) {
                if (data.type == FILTER_TYPE.ORIGINAL) {
                    viewBinding.ivFilterItmNope.show()
                }
                viewBinding.ivFilterItmTransparent.show()
                viewBinding.tvFilterItm.setTextColor(getAppColor(R.color.white))
                viewBinding.tvFilterItm.background = getAppDrawable(R.drawable.shape_purple_bg_corner_bottom_4)
            } else {
                viewBinding.tvFilterItm.background = null
                viewBinding.ivFilterItmNope.gone()
                viewBinding.tvFilterItm.setTextColor(getAppColor(R.color.color_black_54))
                viewBinding.ivFilterItmTransparent.gone()
            }
        }
    }

    class FilterDisplay(var type: FILTER_TYPE, var isSelect: Boolean) {
        fun getImage(): Drawable? {
            return when (type) {
                FILTER_TYPE.ORIGINAL -> getAppDrawable(R.drawable.img_filter_original)
                FILTER_TYPE.SPRING -> getAppDrawable(R.drawable.img_filter_spring)
                FILTER_TYPE.SUMMER -> getAppDrawable(R.drawable.img_filter_summer)
                FILTER_TYPE.FALL -> getAppDrawable(R.drawable.img_filter_fall)
                FILTER_TYPE.WINTER -> getAppDrawable(R.drawable.img_filter_winter)
            }
        }

        fun getTitle(): String {
            return when (type) {
                FILTER_TYPE.ORIGINAL -> getAppString(R.string.original)
                FILTER_TYPE.SPRING -> getAppString(R.string.spring)
                FILTER_TYPE.SUMMER -> getAppString(R.string.summer)
                FILTER_TYPE.FALL -> getAppString(R.string.fall)
                FILTER_TYPE.WINTER -> getAppString(R.string.winter)
            }
        }
    }
}
