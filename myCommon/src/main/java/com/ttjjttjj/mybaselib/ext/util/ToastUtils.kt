package com.ttjjttjj.mybaselib.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * toast的扩展封装，好玩
 */
fun String.toast(context: Context) {
    Toast.makeText(context,this, Toast.LENGTH_SHORT).show()
}

/**
 * show toast
 * @param id strings.xml
 */
fun Context.toast(@StringRes id: Int) {
    Toast.makeText(this, getString(id), Toast.LENGTH_SHORT).show()
}

