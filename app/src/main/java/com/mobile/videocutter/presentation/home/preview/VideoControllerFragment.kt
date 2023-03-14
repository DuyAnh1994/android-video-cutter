package com.mobile.videocutter.presentation.home.preview

import android.annotation.SuppressLint
import android.os.Handler
import android.widget.SeekBar
import com.google.android.exoplayer2.Player
import com.mobile.videocutter.R
import com.mobile.videocutter.base.common.binding.BaseBindingFragment
import com.mobile.videocutter.base.extension.setOnSafeClick
import com.mobile.videocutter.databinding.VideoControllerFragmentBinding
import com.mobile.videocutter.presentation.home.mystudio.ConfirmFragment
import com.mobile.videocutter.presentation.home.mystudio.ShareFragment
import getFormattedTime


class VideoControllerFragment : BaseBindingFragment<VideoControllerFragmentBinding>(R.layout.video_controller_fragment) {
    companion object {
        const val VIDEO_DURATION = "VIDEO_DURATION"
    }

    var player: Player? = null

    override fun onInitView() {
        super.onInitView()
        initOnClick()
        initSeekBar()
    }

    private fun initOnClick() {
        updatePlayPauseButton()
        binding.llVideoControllerBack.setOnSafeClick {
            clearStackFragment()
        }
        binding.ivVideoControllerPlayPause.setOnSafeClick {
            player?.playWhenReady = !(player?.playWhenReady ?: false)
            updatePlayPauseButton()
        }
        binding.ivVideoControllerShare.setOnSafeClick {
            addFragment(ShareFragment())
        }
        binding.ivVideoControllerDelete.setOnSafeClick {
            addFragment(
                ConfirmFragment.Builder()
                    .setTitle(getString(R.string.delete))
                    .setDescription(getString(R.string.delete) + " 3 " + getString(R.string.confirm_delete_description))
                    .setLeftText(getString(R.string.cancel))
                    .setRightText(getString(R.string.delete))
                    .setListener(object : ConfirmFragment.IListener {
                        override fun onConfirm() {
                            // TODO
                        }
                    })
                    .getInstance()
            )
        }
    }

    private fun initSeekBar() {
        // auto update seekbar when video is playing
        val mHandler = Handler()
        baseActivity.runOnUiThread(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (player != null) {
                    binding.sbVideoController.progress = player!!.currentPosition.toInt()
                    binding.tvVideoControllerCurrentTime.text = getFormattedTime(player!!.currentPosition)
                    binding.tvVideoControllerRemainTime.text = "-" + getFormattedTime(player!!.duration - player!!.currentPosition)
                }
                mHandler.postDelayed(this, 10)
            }
        })

        // update video when seekbar is changed
        binding.sbVideoController.apply {
            max = player?.duration?.toInt() ?: 0
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        player?.seekTo(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun updatePlayPauseButton() {
        if (player?.playWhenReady == true) {
            binding.ivVideoControllerPlayPause.setImageResource(R.drawable.ic_white_circle_pause)
        } else {
            binding.ivVideoControllerPlayPause.setImageResource(R.drawable.ic_white_circle_play)
        }
    }
}
