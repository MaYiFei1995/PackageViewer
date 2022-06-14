package com.mai.packageviewer.x

import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo

interface SDKAnalyzerImpl {

    companion object {

        fun PackageInfo.parseSdk(): String {
            val instance = SDKAnalyzer1.instance
            val ret = HashSet<String>()
            instance.providers(this.providers, ret)
            instance.activities(this.activities, ret)
            instance.services(this.services, ret)
            return ret.toString()
        }

    }

    fun providers(providers: Array<ProviderInfo>?, ret: HashSet<String>)

    fun services(services: Array<ServiceInfo>?, ret: java.util.HashSet<String>)

    fun activities(activities: Array<ActivityInfo>?, ret: HashSet<String>)


}