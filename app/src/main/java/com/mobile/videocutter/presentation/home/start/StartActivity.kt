package com.mobile.videocutter.presentation.home.start

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.mobile.videocutter.R
import com.mobile.videocutter.base.common.binding.BaseBindingActivity
import com.mobile.videocutter.base.extension.gone
import com.mobile.videocutter.base.extension.setOnSafeClick
import com.mobile.videocutter.base.extension.show
import com.mobile.videocutter.databinding.StartActivityBinding
import com.mobile.videocutter.domain.model.LocalVideo
import com.mobile.videocutter.domain.model.mockLocalVideoList
import com.mobile.videocutter.presentation.adjust.AdjustActivity
import com.mobile.videocutter.presentation.home.mystudio.MyStudioActivity
import com.mobile.videocutter.presentation.home.mystudio.MyStudioAdapter
import com.mobile.videocutter.presentation.home.mystudio.MyStudioViewModel
import com.mobile.videocutter.presentation.home.preview.PreviewVideoFragment
import com.mobile.videocutter.presentation.home.setting.SettingActivity
import com.mobile.videocutter.presentation.model.IViewListener
import com.mobile.videocutter.presentation.select.selectlibrary.SelectLibraryFolderActivity
import com.mobile.videocutter.presentation.widget.recyclerview.LAYOUT_MANAGER_MODE
import handleUiState

class StartActivity : BaseBindingActivity<StartActivityBinding>(R.layout.start_activity) {
    private val startAdapter = StartAdapter()

    private val viewModel by viewModels<MyStudioViewModel>()

    private val listStartVideo = mockLocalVideoList(50)

    override fun getContainerId(): Int = R.id.constStartContainer

    override fun onInitView() {
        super.onInitView()

        initRecyclerView()
        binding.tvStartSeeAllMyStudio.setOnSafeClick {
            startActivity(Intent(this, MyStudioActivity::class.java))
        }
        binding.ivStartSetting.setOnSafeClick {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.rlStart.setOnSafeClick {
            startActivity(Intent(this, SelectLibraryFolderActivity::class.java))
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            doRequestPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), object : PermissionListener {
                override fun onAllow() {
                    viewModel.getMyStudioVideos()
                }

                override fun onDenied(neverAskAgainPermissionList: List<String>) {}
            })
        } else {
            viewModel.getMyStudioVideos()
        }

        binding.rlStart.setOnSafeClick {
            startActivity(Intent(this, AdjustActivity::class.java))
        }
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()

        lifecycleScope.launchWhenCreated {
            viewModel.myStudioVideoState.collect {
                handleUiState(it, object : IViewListener {
                    override fun onSuccess() {
                        startAdapter.submitList(it.data?.map { video -> MyStudioAdapter.VideoDisplay(video) })
                    }
                })
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            clearStackFragment()
        } else {
            super.onBackPressed()
        }
    }

    private fun initRecyclerView() {
        startAdapter.listener = object : StartAdapter.IListener {
            override fun onVideoClick(localVideo: LocalVideo?) {
                replaceFragment(PreviewVideoFragment(), bundleOf(PreviewVideoFragment.VIDEO_PATH to localVideo?.videoPath, PreviewVideoFragment.VIDEO_DURATION to localVideo?.duration))
            }
        }
        binding.crvStartVideoList.setAdapter(startAdapter)
        binding.crvStartVideoList.setLayoutManagerMode(LAYOUT_MANAGER_MODE.LINEAR_HORIZATION)

        if (listStartVideo.isEmpty()) {
            binding.crvStartVideoList.gone()
            binding.llStartNoData.show()
        } else {
            binding.crvStartVideoList.show()
            binding.llStartNoData.gone()
        }
    }
}
