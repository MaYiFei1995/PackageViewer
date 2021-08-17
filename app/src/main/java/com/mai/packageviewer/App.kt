package com.mai.packageviewer

import android.app.Application

class App : Application() {
    companion object {
        /**
         * 全局context
         */
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}