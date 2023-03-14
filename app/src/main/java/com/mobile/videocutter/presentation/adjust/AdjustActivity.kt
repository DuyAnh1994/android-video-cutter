package com.mobile.videocutter.presentation.adjust

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.mobile.videocutter.R
import com.mobile.videocutter.base.common.binding.BaseBindingActivity
import com.mobile.videocutter.base.extension.getAppDimension
import com.mobile.videocutter.base.extension.getAppDrawable
import com.mobile.videocutter.databinding.AdjustActivityBinding
import com.mobile.videocutter.domain.model.LocalVideo
import com.mobile.videocutter.presentation.model.IViewListener
import com.mobile.videocutter.presentation.widget.recyclerview.CustomRecyclerView
import com.mobile.videocutter.presentation.widget.recyclerview.LAYOUT_MANAGER_MODE
import com.mobile.videocutter.presentation.widget.video.videoplayercontrol.VideoPlayerControl
import handleUiState

class AdjustActivity : BaseBindingActivity<AdjustActivityBinding>(R.layout.adjust_activity) {
    companion object {
        const val LIST_VIDEO = "list_video"
    }

    private val viewModel by viewModels<AdjustViewModel>()
    private val adapter = AdjustAdapter()
    private var isPlay = false

    override fun onInitView() {
        super.onInitView()

        binding.hvAdjust.apply {
            setTextViewRightPadding(
                getAppDimension(R.dimen.dimen_14),
                getAppDimension(R.dimen.dimen_4),
                getAppDimension(R.dimen.dimen_14),
                getAppDimension(R.dimen.dimen_4)
            )

            setTextViewRightMargin(
                getAppDimension(R.dimen.dimen_10),
                getAppDimension(R.dimen.dimen_4),
                getAppDimension(R.dimen.dimen_10),
                getAppDimension(R.dimen.dimen_4)
            )

            getAppDrawable(R.drawable.shape_purple_bg_corner_6)?.let { setBackgroundTextViewRight(it) }

            setOnLeftIconClickListener {
                onBackPressed()
            }
        }

        binding.crvAdjust.apply {
            setAdapter(adapter)
            setLayoutManagerMode(LAYOUT_MANAGER_MODE.LINEAR_HORIZATION)
            setDragRecyclerView()
            listener = object : CustomRecyclerView.IListener {
                override fun onScroll(position: Int) {
                    binding.crvAdjust.smoothiePosition(position)
                }
            }
        }

        binding.vpcAdjust.apply {

            setUrl(
                listOf(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                )
            )

            listener = object : VideoPlayerControl.IListener {
                override fun onPlayerReady(player: ExoPlayer) {
                    binding.pvAdjust.player = player
                }

                override fun onPlayerEnd(isPlay: Boolean) {
                    this@AdjustActivity.isPlay = isPlay
                }
            }

            setOnLeftIconClickListener {
                isPlay = !isPlay
                if (isPlay) {
                    resumePlayer()
                } else {
                    pausePlayer()
                }
            }
        }

        adapter.listener = object : AdjustAdapter.IListener {

            override fun onLoadLocalVideoDefault(localVideo: LocalVideo) {
                // load video đầu tiên
//                binding.vpcAdjust.setUrl(STRING_DEFAULT)
            }

            override fun onDelete(localVideo: LocalVideo) {
                viewModel.deleteLocalVideo(localVideo)
            }

            override fun onClick(localVideo: LocalVideo) {
                isPlay = false
//                binding.vpcAdjust.apply {
//                    setUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
//                    pausePlayer()
//                }
            }
        }
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()

        lifecycleScope.launchWhenResumed {
            viewModel.localVideoAdjust.collect {
                handleUiState(it, object : IViewListener {
                    override fun onSuccess() {
                        binding.crvAdjust.submitList(it.data)
                    }
                })
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStop() {
        super.onStop()
        isPlay = false
        binding.vpcAdjust.pausePlayer()
    }

    override fun onCleaned() {
        super.onCleaned()
        binding.vpcAdjust.stopPlayer()
    }
}
