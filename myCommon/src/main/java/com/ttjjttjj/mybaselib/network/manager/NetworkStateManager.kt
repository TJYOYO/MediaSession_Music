package com.ttjjttjj.mybaselib.network.manager

import com.ttjjttjj.mybaselib.livedata.event.EventLiveData

/**
 * 网络变化管理者
 */
class NetworkStateManager private constructor(){

    val mNetworkStateCallback = EventLiveData<NetState>()

    companion object {
        val instance : NetworkStateManager by lazy (mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }
}