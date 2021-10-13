package com.ttjjttjj.mybaselib.ext.util

object TimeUtils {

    /**
     * 秒转化为00:00形式
     * @param curPosition
     */
    fun setTimeByZero(curPosition: Long): String {
        val cm = curPosition / 1000 / 60
        val cs = curPosition / 1000 % 60
        val builder = StringBuilder()
        return builder.append(cm / 10).append(cm % 10).append(":")
            .append(cs / 10).append(cs % 10).toString()
    }
}