package com.ttjjttjj.mybaselib.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ttjjttjj.mybaselib.ext.extensions.logDebug

/**
 * 方便观察fragment的生命周期
 */
open class LifeFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logDebug("onAttach: --->")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logDebug("onCreate: --->")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logDebug("onCreateView: --->")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logDebug("onViewCreated: --->")
    }

    override fun onStart() {
        super.onStart()
        logDebug("onStart: --->")
    }

    override fun onPause() {
        super.onPause()
        logDebug("onPause: --->")
    }

    override fun onStop() {
        super.onStop()
        logDebug("onStop: --->")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logDebug("onDestroyView: --->")
    }





}