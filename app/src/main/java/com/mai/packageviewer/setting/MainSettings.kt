package com.mai.packageviewer.setting

import android.content.Context
import android.content.SharedPreferences
import com.mai.packageviewer.App

enum class MainSettings {
    INSTANCE;

    companion object {
        private const val FILE_NAME = "MainSettings"
        const val ORDER_BY_NAME = "order_by_name"

        // 属性过滤
        const val SHOW_SYSTEM_APP = "show_system_app"
        const val SHOW_RELEASE_APP = "show_release_app"
        const val SHOW_DEBUG_APP = "show_debug_app"
        const val SHOW_TEST_ONLY_APP = "show_test_only_app"
        const val SHOW_GAME_APP = "show_game_app"

        // 框架过滤
        const val SHOW_APPS_PLAT = "show_apps_plat"
        const val SHOW_ALL_APPS = "show_all_apps"
        const val SHOW_API_CLOUD_APPS = "show_api_cloud_apps"
        const val SHOW_DCLOUD_APPS = "show_dcloud_apps"
        const val SHOW_YI_MEN_APPS = "show_yi_men_apps"
        const val SHOW_FLUTTER_APPS = "show_flutter_apps"
        const val SHOW_RN_APPS = "show_rn_apps"
        const val INIT_FAST_MODE = "init_fast_mode"
    }

    fun getBool(key: String, defaultValue: Boolean): Boolean {
        return getSp().getBoolean(key, defaultValue)
    }

    fun setBool(key: String, value: Boolean) {
        getSp().edit().putBoolean(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return getSp().getString(key, defaultValue)!!
    }

    fun setString(key: String, value: String) {
        getSp().edit().putString(key, value).apply()
    }

    private fun getSp(): SharedPreferences {
        return App.app.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

}