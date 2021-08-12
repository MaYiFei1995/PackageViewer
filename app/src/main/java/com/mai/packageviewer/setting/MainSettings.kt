package com.mai.packageviewer.setting

import android.content.Context
import android.content.SharedPreferences
import com.mai.packageviewer.App

enum class MainSettings {
    INSTANCE;

    companion object {
        private const val FILE_NAME = "MainSettings"
        const val ORDER_BY_NAME = "order_by_name"
        const val SHOW_SYSTEM_APP = "show_system_app"
        const val SHOW_RELEASE_APP = "show_release_app"
        const val SHOW_DEBUG_APP = "show_debug_app"
        const val SHOW_TEST_ONLY_APP = "show_test_only_app"
        const val SHOW_GAME_APP = "show_game_app"
    }

    fun getBool(key: String, defaultValue: Boolean): Boolean {
        return getSp().getBoolean(key, defaultValue)
    }

    fun setBool(key: String, value: Boolean) {
        getSp().edit().putBoolean(key, value).apply()
    }

    private fun getSp(): SharedPreferences {
        return App.app.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

}