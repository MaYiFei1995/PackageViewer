package com.mai.packageviewer.x

import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo

object SDKAnalyzer {

    fun PackageInfo.parseSdk(): String {
        val ret = HashSet<String>()
        providers(this.providers, ret)
        activities(this.activities, ret)
        services(this.services, ret)
        return ret.toString()
    }

    private fun services(services: Array<ServiceInfo>?, ret: java.util.HashSet<String>) {
        services?.forEach {
            when (it.name) {

            }
        }
    }

    private fun providers(providers: Array<ProviderInfo>?, ret: HashSet<String>) {
        providers?.forEach {
            when (it.name) {

            }
        }
    }

    private fun activities(activities: Array<ActivityInfo>?, ret: HashSet<String>) {
        activities?.forEach {
            when (it.name) {

            }
        }
    }

}