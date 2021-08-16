package com.mai.packageviewer.util

import android.content.pm.PackageInfo
import android.util.Log
import com.mai.packageviewer.data.AppInfo
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

object AppInfoHelper {
    var isRunning = false

    var threadList = ConcurrentHashMap<Int, Thread>()

    fun handle(list: MutableList<PackageInfo>, callback: AppInfoCallback, threads: Int = 4) {
        Log.e("test", "threads = $threads")
        isRunning = true
        val ret = Vector<MutableList<AppInfo>>(threads)
        val averageAssign = averageAssign(list, threads)
        for (i in 0 until threads) {
            val thread = Thread {
                val result = ArrayList<AppInfo>(averageAssign[i].size)
                averageAssign[i].forEach {
                    val appInfo = AppInfo(it)
                    result.add(appInfo)
                }
                ret.add(result)
                if (ret.size == threads) {
                    isRunning = false
                    callback.onResult(ret)
                    threadList.clear()
                } else {
                    try {
                        threadList.remove(i)
                    } catch (ignore: Exception) {

                    }
                }
            }
            threadList[i] = thread
            thread.start()
        }
    }

    private fun <T> averageAssign(source: List<T>, n: Int): List<List<T>> {
        val result: MutableList<List<T>> = ArrayList()
        var remainder = source.size % n
        val number = source.size / n
        var offset = 0
        for (i in 0 until n) {
            val value: List<T> =
                if (remainder > 0) {
                    val ret = source.subList(i * number + offset, (i + 1) * number + offset + 1)
                    remainder--
                    offset++
                    ret
                } else {
                    source.subList(i * number + offset, (i + 1) * number + offset)
                }
            result.add(value)
        }
        return result
    }

    fun forceStop() {
        threadList.forEach { (_, v) ->
            try {
                if (v.isAlive) {
                    v.interrupt()
                }
            } catch (ignore: Exception) {

            }
        }
    }

    interface AppInfoCallback {
        fun onResult(ret: Vector<MutableList<AppInfo>>)
    }
}