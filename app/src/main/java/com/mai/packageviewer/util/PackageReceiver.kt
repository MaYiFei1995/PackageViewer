package com.mai.packageviewer.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageReceiver(private val action: (context: Context, intent: Intent) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = action(context, intent)
}

