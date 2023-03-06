package ai.ftech.base.common.binding

import com.mobile.videocutter.base.common.BaseDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingDialog<DB : ViewDataBinding>(layoutId: Int) : BaseDialog(layoutId) {
    protected val binding get() = _binding!!
    private var _binding: DB? = null

    override fun attachView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(myInflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}
