package com.mobile.videocutter

import android.util.Log
import com.mobile.videocutter.base.common.binding.BaseBindingActivity
import com.mobile.videocutter.databinding.ActivityMainBinding

class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onInitView() {
        super.onInitView()
        Log.d("AAA", "onInitView: AAA")
    }
}