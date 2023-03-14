package com.mobile.videocutter.base.common

import ai.ftech.base.common.navigation.FadeAnim
import ai.ftech.base.common.navigation.IScreenAnim
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.InflateException
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseActivity(@LayoutRes protected val layoutId: Int) : AppCompatActivity(), BaseView {
    companion object {
        const val FRAGMENT_NAME = "FRAGMENT_NAME"
        const val FRAGMENT_BUNDLE = "FRAGMENT_BUNDLE"
    }

    protected val TAG = this::class.java.simpleName
    private var permissionListener: PermissionListener? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val listDenied = mutableListOf<String>()
            val listNeverAskAgain = mutableListOf<String>()
            it.forEach { (k, v) ->
                if (!v) {
                    if (shouldShowRequestPermissionRationale(k)) {
                        listDenied.add(k)
                    } else {
                        listNeverAskAgain.add(k)
                    }
                }
            }
            if (listDenied.isEmpty() && listNeverAskAgain.isEmpty()) {
                permissionListener?.onAllow()
            } else {
                permissionListener?.onDenied(listNeverAskAgain)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onPrepareInitView()
        super.onCreate(savedInstanceState)
        try {
            attachView()
            onInitBinding()
            onInitView()
            onObserverViewModel()
        } catch (e: InflateException) {
            e.printStackTrace()
            Log.e(TAG, "${e.message}")
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onCleaned()
    }

    override fun onInitView() {}

    open fun attachView() {
        setContentView(layoutId)
    }

    open fun getContainerId(): Int = LAYOUT_INVALID

    fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    fun doRequestPermission(permissions: Array<String>, listener: PermissionListener) {
        permissionListener = listener
        launcher.launch(permissions)
    }

    fun backFragment() {
        supportFragmentManager.popBackStack()
    }

    fun replaceFragment(
        fragment: BaseFragment,
        bundle: Bundle? = null,
        keepToBackStack: Boolean = true,
        screenAnim: IScreenAnim = FadeAnim()
    ) {
        includeFragment(
            fragment,
            bundle,
            getContainerId(),
            true,
            keepToBackStack,
            screenAnim
        )
    }

    fun addFragment(
        fragment: BaseFragment,
        bundle: Bundle? = null,
        keepToBackStack: Boolean = true,
        screenAnim: IScreenAnim = FadeAnim()
    ) {
        includeFragment(
            fragment,
            bundle,
            getContainerId(),
            false,
            keepToBackStack,
            screenAnim
        )
    }

    fun clearStackFragment() {
        supportFragmentManager.let { fm ->
            fm.backStackEntryCount.let { count ->
                for (i in 0..count) {
                    fm.popBackStack()
                }
            }
        }
    }

    fun getCurrentFragment(): Fragment? {
        val fragmentList = supportFragmentManager.fragments
        return fragmentList.lastOrNull()
    }

    private fun includeFragment(
        fragment: Fragment,
        bundle: Bundle?,
        containerId: Int,
        isReplace: Boolean,
        keepToBackStack: Boolean,
        screenAnim: IScreenAnim
    ) {
        if (getContainerId() == LAYOUT_INVALID) {
            throw IllegalArgumentException("Cần phải gán container id để replace fragment")
        }
        try {
            val tag = fragment::class.java.simpleName
            bundle?.let {
                fragment.arguments = it
            }
            supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    screenAnim.enter(),
                    screenAnim.exit(),
                    screenAnim.popEnter(),
                    screenAnim.popExit()
                )
                if (isReplace) {
                    replace(containerId, fragment, tag)
                } else {
                    add(containerId, fragment, tag)
                }
                if (keepToBackStack) {
                    addToBackStack(tag)
                }
                commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface PermissionListener {
        fun onAllow()
        fun onDenied(neverAskAgainPermissionList: List<String>)
    }
}
