package com.mobile.videocutter.presentation.widget.video.videoplayercontrol

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.mobile.videocutter.AppConfig
import com.mobile.videocutter.R
import com.mobile.videocutter.base.extension.*
import getFormattedTime


class VideoPlayerControl constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {
    private val TAG = "VideoPlayerControl"

    //left
    private var flLeft: FrameLayout? = null
    private var ivLeft: ImageView? = null
    private var leftIcon: Drawable? = null
    private var isShowLeftIcon: Boolean = false

    private var tvLeft: TextView? = null
    private var leftText: CharSequence? = null
    private var isShowLeftText: Boolean = false

    private var onLeftIconClick: (() -> Unit)? = null

    //center
    private var sbSeekBar: SeekBar? = null
    private var isShowSeekBar: Boolean = false
    private var progress = 0

    private var onSeekBarClick: (() -> Unit)? = null

    // right
    private var tvRight: TextView? = null
    private var rightText: CharSequence? = null
    private var isShowRightText: Boolean = false

    // player
    private var player: ExoPlayer? = null

    // control player
    private var _handler: Handler? = null
    private var _runnable: Runnable? = null

    // source player
    private var url: String = STRING_DEFAULT
    private var mediaItemList: MutableList<MediaItem> = arrayListOf()

    // callback
    var listener: IListener? = null
    var seeBarListener: IListener.ISeeBarListener? = null

    var total = 0L
    val window = Timeline.Window()
    val mediaItems = ArrayList<MediaItem>()
    var concatenatingMediaSource: ConcatenatingMediaSource? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.video_player_control_layout, this, true)
        initView(attrs)
        _handler = Handler(Looper.getMainLooper())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initPlayer()
    }

    var listDuration = ArrayList<Long>()
    override fun onFinishInflate() {
        super.onFinishInflate()
        flLeft = findViewById(R.id.flVideoPlayerControl)
        ivLeft = findViewById(R.id.ivVideoPlayerControlLeftIcon)
        tvLeft = findViewById(R.id.tvVideoPlayerControlLeftText)

        sbSeekBar = findViewById(R.id.sbVideoPlayerControlSeekBar)

        tvRight = findViewById(R.id.tvVideoPlayerControlRightText)

        //left
        if (isShowLeftIcon) {
            ivLeft?.show()
        } else {
            ivLeft?.gone()
        }

        if (leftIcon != null) {
            ivLeft?.setImageDrawable(leftIcon)
            ivLeft?.show()
        } else {
            ivLeft?.gone()
        }

        if (!TextUtils.isEmpty(leftText)) {
            tvLeft?.text = leftText
        }

        if (isShowLeftText) {
            tvLeft?.show()
        } else {
            tvLeft?.gone()
        }

        flLeft?.setOnSafeClick {
            onLeftIconClick?.invoke()
        }

        //center
        if (sbSeekBar == null) {
            sbSeekBar?.gone()
        } else {
            sbSeekBar?.show()
        }

        if (isShowSeekBar) {
            sbSeekBar?.show()
        } else {
            sbSeekBar?.gone()
        }

        sbSeekBar?.setOnSafeClick {
            onSeekBarClick?.invoke()
        }

        //right
        if (!TextUtils.isEmpty(rightText)) {
            tvRight?.text = rightText
        }

        if (isShowRightText) {
            tvRight?.show()
        } else {
            tvRight?.gone()
        }

        sbSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null && fromUser && player != null && concatenatingMediaSource != null) {
                    this@VideoPlayerControl.progress = progress
                    tvLeft?.text = getFormattedTime(progress.toLong())
                    seeBarListener?.onSeeBarProgreesListener(seekBar, progress, fromUser)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    _handler?.removeCallbacksAndMessages(null)
                    seeBarListener?.onSeeBarStartTrackingTouch(seekBar)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null && player != null) {

                    resumePlayer()
                    seeBarListener?.onSeeBaronStopTrackingTouch(seekBar)
                }
            }
        })
    }

    //    fun set
    private fun initPlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(this.context).build()
        }
        listener?.onPlayerReady(player!!)
    }

    var currentVideo = 0
    fun setUrl(listUrl: List<String>) {
        initPlayer()
        currentVideo = 0
        if (listUrl.isEmpty()) {
            // xóa toàn bộ runable
            _handler?.removeCallbacksAndMessages(null)
            tvRight?.text = getAppString(R.string.time_start)
            sbSeekBar?.progress = INT_DEFAULT
            if (player?.isPlaying == true) {
                player?.seekToDefaultPosition()
            }
            player?.pause()
        } else {
            val listUri = listUrl.map {
                Uri.parse(it)
            }

            concatenatingMediaSource = buildMediaSource(listUri as ArrayList<Uri>)
            player?.setMediaSource(concatenatingMediaSource!!)

            player?.addListener(object : Player.Listener {

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    super.onTimelineChanged(timeline, reason)
                    Log.d(TAG, "onTimelineChanged: ${timeline.windowCount}")

                    for (i in 0 until timeline.windowCount) {
                        if (i == 0) {
                            total = 0
                        }
                        val durations = timeline.getWindow(i, window).durationMs
                        Log.d(TAG, "onTimelineChanged $i: ${durations}")
                        if (durations != C.TIME_UNSET) {
                            total += durations
                        }
                    }
                    tvRight?.text = getFormattedTime(total)
                    sbSeekBar?.max = total.toInt()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        STATE_READY -> {
                            Log.d(TAG, "onPlaybackStateChanged: ${concatenatingMediaSource!!.initialTimeline.windowCount}")
                            _runnable = object : Runnable {
                                override fun run() {
                                    Log.d(TAG, "run: ${currentVideo}")
                                    val duration = concatenatingMediaSource!!.initialTimeline.getWindow(currentVideo, window).durationMs
                                    if (duration != C.TIME_UNSET) {
                                        tvLeft?.text = getFormattedTime(player!!.currentPosition + duration)
                                        sbSeekBar?.progress = (player!!.currentPosition.toInt() + duration).toInt()
                                    }
                                    _handler?.postDelayed(this, AppConfig.TIME_CONFIG)
                                }
                            }
                        }

                        STATE_ENDED -> {
                            pausePlayer(STATE_ENDED)
                            listener?.onPlayerEnd(false)
                        }
                    }
                }

                override fun onPositionDiscontinuity(oldPosition: PositionInfo, newPosition: PositionInfo, reason: Int) {
                    super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                    currentVideo++
                }
            })
            player?.prepare()
        }
    }

    private fun buildMediaSource(uris: ArrayList<Uri>): ConcatenatingMediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(this.context)

        val progressiveMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        val mediaSources = ArrayList<MediaSource>()

        for (uri in uris) {
            mediaSources.add(progressiveMediaSource.createMediaSource(MediaItem.fromUri(uri)))
        }

        val concatenatingMediaSource = ConcatenatingMediaSource()
        concatenatingMediaSource.addMediaSources(mediaSources)

        return concatenatingMediaSource
    }

    fun stopPlayer() {
        _handler?.removeCallbacksAndMessages(null)
        _handler = null

        sbSeekBar!!.progress = INT_DEFAULT
        player?.seekToDefaultPosition()
        player?.stop()
        player?.release()
        player = null
    }

    fun pausePlayer(state: Int = 0) {
        if (state == STATE_ENDED) {
            // reset player
            player?.seekTo(0)
            player?.playWhenReady = false
            player?.seekToDefaultPosition(0)
            // update ui khi end video
            tvLeft?.text = getAppString(R.string.time_start)
            sbSeekBar?.progress = INT_DEFAULT
        }
        // xóa toàn bộ runable
        _handler?.removeCallbacksAndMessages(null)

        ivLeft?.setImageDrawable(getAppDrawable(R.drawable.ic_black_pause))
        player?.pause()
    }

    fun resumePlayer(progress: Long = LONG_DEFAULT) {
        if (_runnable != null) {
            _handler?.postDelayed(_runnable!!, AppConfig.TIME_CONFIG)
        }
        ivLeft?.setImageDrawable(getAppDrawable(R.drawable.ic_black_play))
        if (progress != LONG_DEFAULT) {
            player?.seekTo(progress)
        }
        player?.play()
    }

    fun setLeftText(text: CharSequence?) {
        leftText = text
        this.tvLeft?.text = leftText
    }

    fun setLeftText(text: String?) {
        this.tvLeft?.text = text
    }

    fun showLeftText(isShow: Boolean) {
        if (isShow) {
            this.tvLeft?.show()
        } else {
            this.tvLeft?.gone()
        }
    }

    fun showLeftIcon(isShow: Boolean) {
        if (isShow) {
            this.ivLeft?.show()
        } else {
            this.ivLeft?.gone()
        }
    }

    fun showSeekBar(isShow: Boolean) {
        if (isShow) {
            this.sbSeekBar?.show()
        } else {
            this.sbSeekBar?.gone()
        }
    }

    fun setRightText(text: CharSequence?) {
        rightText = text
        this.tvRight?.text = rightText
    }

    fun setRightText(text: String?) {
        this.tvRight?.text = text
    }

    fun showRightText(isShow: Boolean) {
        if (isShow) {
            this.tvRight?.show()
        } else {
            this.tvRight?.gone()
        }
    }

    fun setLeftIcon(res: Int) {
        leftIcon = ContextCompat.getDrawable(context, res)
        this.ivLeft?.setImageDrawable(leftIcon)
    }

    fun setOnLeftIconClickListener(onClick: (() -> Unit)?) {
        this.onLeftIconClick = onClick
    }

    fun setOnSeekBarClickListener(onClick: (() -> Unit)?) {
        this.onSeekBarClick = onClick
    }

    private fun initView(attrs: AttributeSet?) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.VideoPlayerControl, 0, 0)

        // left
        if (ta.hasValue(R.styleable.VideoPlayerControl_vpc_left_icon)) {
            leftIcon = ta.getDrawable(R.styleable.VideoPlayerControl_vpc_left_icon)
        }

        if (ta.hasValue(R.styleable.VideoPlayerControl_vpc_left_icon_show)) {
            isShowLeftIcon = ta.getBoolean(R.styleable.VideoPlayerControl_vpc_left_icon_show, true)
        }

        if (ta.hasValue(R.styleable.VideoPlayerControl_vpc_left_text)) {
            leftText = ta.getString(R.styleable.VideoPlayerControl_vpc_left_text)
        }

        if (ta.hasValue(R.styleable.VideoPlayerControl_vpc_left_text_show)) {
            isShowLeftText = ta.getBoolean(R.styleable.VideoPlayerControl_vpc_left_text_show, true)
        }

        if (ta.hasValue(R.styleable.VideoPlayerControl_vpc_seekbar_show)) {
            isShowSeekBar = ta.getBoolean(R.styleable.VideoPlayerControl_vpc_seekbar_show, true)
        }

        //right
        if (ta.hasValue(R.styleable.VideoPlayerControl_vpc_right_text)) {
            rightText = ta.getString(R.styleable.VideoPlayerControl_vpc_right_text)
        }

        if (ta.hasValue(R.styleable.VideoPlayerControl_vpc_right_text_show)) {
            isShowRightText = ta.getBoolean(R.styleable.VideoPlayerControl_vpc_right_text_show, true)
        }

        ta.recycle()
    }

    interface IListener {

        fun onPlayerReady(player: ExoPlayer)
        fun onPlayerEnd(isPlay: Boolean)

        interface ISeeBarListener {
            fun onSeeBarProgreesListener(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            fun onSeeBarStartTrackingTouch(seebar: SeekBar) {}
            fun onSeeBaronStopTrackingTouch(seekBar: SeekBar) {}
        }
    }
}
