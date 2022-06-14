package com.mai.packageviewer.x

import android.content.pm.ActivityInfo
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo

class SDKAnalyzer1 : SDKAnalyzerImpl {

    companion object {
        val instance: SDKAnalyzerImpl by lazy {
            SDKAnalyzer1()
        }
    }

    override fun services(services: Array<ServiceInfo>?, ret: java.util.HashSet<String>) {
        services?.forEach {
            when (it.name) {
                "com.a.b.CService" ->
                    ret.add("CService")
                "com.d.e.FService" ->
                    ret.add("FService")
            }
        }
    }

    override fun providers(providers: Array<ProviderInfo>?, ret: HashSet<String>) {
        providers?.forEach {
            when (it.name) {
                "com.a.b.CProvider" ->
                    ret.add("CProvider")
                "com.d.e.FProvider" ->
                    ret.add("FProvider")
            }
        }
    }

    override fun activities(activities: Array<ActivityInfo>?, ret: HashSet<String>) {
        activities?.forEach {
            when (it.name) {
                "com.a.b.CActivity",
                "com.a.b.DActivity" ->
                    ret.add("com.a.b")

                "com.e.f.GActivity" ->
                    ret.add("com.e.f")
            }
        }
    }
}
