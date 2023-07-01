package com.exert.wms.mvvmbase

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.exert.wms.R

abstract class NavigationFragment<VM : BaseViewModel, VB : ViewDataBinding> :
    MVVMFragment<VM, VB>(), BackListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                updateTitleBar()
            }
        }, true)

        childFragmentManager.addOnBackStackChangedListener {
            updateTitleBar()
        }
    }

    override fun onResume() {
        super.onResume()
        updateTitleBar()
    }

    private fun updateTitleBar() {
        val childTitle = (getCurrentChildFragment() as MVVMFragment<*, *>?)?.title
        val childTitleString = (getCurrentChildFragment() as MVVMFragment<*, *>?)?.titleString

        val titleText = childTitle ?: title
        val titleStringText = childTitleString ?: titleString
        if (titleStringText != null) {
            activity?.title = titleStringText
        } else if (titleText != null) {
            activity?.setTitle(titleText)
        }
    }

    private fun getCurrentChildFragment(): Fragment? {
        return try {
            childFragmentManager.findFragmentById(R.id.navigationContainer)
        } catch (e: java.lang.Exception) {
            null
        }
    }

    override fun onBackPressed(): Boolean {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            return true
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun initChildFragment(fragment: Fragment) {
        if (childFragmentManager.fragments.none() {
                fragment.javaClass.isInstance(it)
            })
            childFragmentManager.beginTransaction()
                .replace(R.id.navigationContainer, fragment)
                .commit()
    }

}