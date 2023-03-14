package com.mobile.videocutter.presentation.tasselsvideo

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.mobile.videocutter.R
import com.mobile.videocutter.base.common.binding.BaseBindingActivity
import com.mobile.videocutter.base.extension.getAppDimension
import com.mobile.videocutter.base.extension.getAppDrawable
import com.mobile.videocutter.databinding.TasselsVideoActivityBinding
import com.mobile.videocutter.domain.model.ToolVideo
import com.mobile.videocutter.domain.model.mockToolVideo
import com.mobile.videocutter.presentation.widget.recyclerview.LAYOUT_MANAGER_MODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasselsVideoActivity : BaseBindingActivity<TasselsVideoActivityBinding>(R.layout.tassels_video_activity) {

    companion object {
        const val VIDEO_PATH = "VIDEO_PATH"
    }

    private var player: ExoPlayer? = null

    private val viewModel by viewModels<TasselsVideoViewModel>()

    private val adapter by lazy {
        TasselsVideoAdapter()
    }

    override fun onInitView() {
        super.onInitView()

        viewModel.videoPath = intent.getStringExtra(VIDEO_PATH)

        initView()
        initializePlayer()

        binding.crvTasselsVideoTool.apply {
            setAdapter(adapter)
            setLayoutManagerMode(LAYOUT_MANAGER_MODE.LINEAR_HORIZATION)
            submitList(mockToolVideo())
        }
    }

    private fun initView() {
        binding.hvTasselsVideoRoot.apply {
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
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.pvTasselsVideoPlay.player = exoPlayer

                val mediaItem = viewModel.videoPath?.let { MediaItem.fromUri(it) }
                if (mediaItem != null) {
                    exoPlayer.setMediaItem(mediaItem)
                }

                exoPlayer.playWhenReady = true
                exoPlayer.seekTo(0)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player.let { exoPlayer ->
            viewModel.playbackPosition = exoPlayer?.currentPosition
            viewModel.currentItem = exoPlayer?.currentMediaItemIndex
            viewModel.playWhenReady = exoPlayer?.playWhenReady
            exoPlayer?.release()
        }
    }
}
