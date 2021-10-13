package com.ttjjttjj.mybaselib.ext

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ttjjttjj.mybaselib.ext.extensions.logDebug

/**
 * 切换fragment： 先hide所有已经add的，然后判断是否add
 */
fun FragmentActivity.switchFragment(targetFragment: Fragment, @IdRes idRes: Int) {
    val transaction = supportFragmentManager.beginTransaction()

    // 先隐藏其他的
    supportFragmentManager.fragments.forEach {
        logDebug("switchFragment before hide fragment=$it")
        transaction.hide(it)
    }
    if (targetFragment.isAdded) {
        logDebug("show targetFragment=$targetFragment")
        transaction.show(targetFragment)
    } else {
        logDebug("add targetFragment=$targetFragment")
        transaction.add(idRes, targetFragment)
    }
    transaction.commitAllowingStateLoss() // ???
    supportFragmentManager.executePendingTransactions()
}

/**
 * add - fragment操作
 */
fun FragmentActivity.addFragment(targetFragment: Fragment,@IdRes idRes: Int) {
    val transaction = supportFragmentManager.beginTransaction()
    if (!targetFragment.isAdded) {
        transaction.add(idRes, targetFragment)
        logDebug("addFragment =$targetFragment")
        transaction.commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    }
}

/**
 * remove - fragment操作
 */
fun FragmentActivity.removeFragment(targetFragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    if (targetFragment.isAdded) {
        transaction.remove(targetFragment)
        logDebug("removeFragment =$targetFragment")
    }
    transaction.commitAllowingStateLoss()
    supportFragmentManager.executePendingTransactions()
}

fun FragmentActivity.getCurrentFragment() {

}


/*
fun FragmentActivity.changeOrCreateFragment(targetFragment: Fragment, @IdRes idRes: Int) {  //通过显示隐藏显示Fragment
    try {
        val tag = targetFragment::class.java.name
        logDebug( "changeOrCreateFragment tag --- $tag")
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragments: List<Fragment> = supportFragmentManager.fragments
        var targetFragment: Fragment? = supportFragmentManager.findFragmentByTag(tag)
        if (targetFragment != null) {
            logDebug("targetFragment != null")
            for (f in fragments) {
                if (f === targetFragment) {
                    logDebug("f == targetFragment" + f.tag)
                    transaction.show(f)
                } else {
                    logDebug("f == targetFragment, hide" + f.tag)
                    transaction.hide(f)
                }
            }
        } else {
            for (f in fragments) {
                transaction.hide(f)
                logDebug("f == targetFragment, hide" + f.tag)
            }
            targetFragment = createFragmentByTag(targetFragment)
            logDebug( "createFragmentByTag tag ===" + targetFragment!!.getTag())
            if (!targetFragment!!.isAdded) {
                logDebug("add targetFragment tag --- $tag")
                transaction.add(idRes, targetFragment, tag)
            } else {
                logDebug("add targetFragment tag show --- $tag")
                transaction.show(targetFragment!!)
            }
        }
        transaction.commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()
    } catch (e: Exception) {
        e.printStackTrace()
        logDebug("changeOrCreateFragment Exception --- " + e.message)
    }
}
*/


/*
fun createFragmentByTag(tag: String?): Fragment? {
    var fragment: Fragment? = null
    when (tag) {
          -> {
            if (networkFragment == null) {
                networkFragment = NetworkFragment()
            }
            fragment = networkFragment
        }
        */
/*MainActivity.DISPLAY -> {
            if (displayFragment == null) {
                displayFragment = DisplayFragment()
            }
            fragment = displayFragment
        }*//*

        else -> {
        }
    }
    return fragment
}*/
