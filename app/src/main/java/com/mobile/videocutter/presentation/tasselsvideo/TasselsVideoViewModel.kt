package com.mobile.videocutter.presentation.tasselsvideo

import com.mobile.videocutter.base.common.BaseViewModel

class TasselsVideoViewModel : BaseViewModel() {

    var videoPath: String? = null
    var playbackPosition: Long? = null
    var currentItem: Int? = null
    var playWhenReady: Boolean? = null
}
