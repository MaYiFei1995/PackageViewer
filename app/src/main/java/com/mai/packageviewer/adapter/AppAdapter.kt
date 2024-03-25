package com.mai.packageviewer.adapter

import android.annotation.SuppressLint
import android.widget.*
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mai.packageviewer.R
import com.mai.packageviewer.data.AppInfo
import com.mai.packageviewer.view.AppInfoDetailDialog
import java.util.*

/**
 * 主应用列表的Adapter
 * 显示应用名、图标、包名和版本号
 */
class AppAdapter(data: MutableList<AppInfo>) :
    BaseQuickAdapter<AppInfo, BaseViewHolder>(R.layout.item_app_info, data), Filterable {

    // 用于搜索的data，需要在变更筛选条件时更新
    private var rawData: MutableList<AppInfo> = data

    override fun convert(holder: BaseViewHolder, item: AppInfo) {
        holder.setText(R.id.appName, item.label)
        if (item.iconDrawable != null) {
            (holder.getView(R.id.icon) as ImageView).let {
                Glide.with(it).load(item.iconDrawable).into(it)
            }
        }

        holder.setText(R.id.packageName, item.packageName)
        holder.setText(R.id.version, item.versionName)

        holder.getView<Button>(R.id.detail).setOnClickListener {
            // 详情页
            AppInfoDetailDialog(context, item)
        }
    }

    /**
     * 变更待搜索的data，需要在变更筛选条件时手动更新
     * 可以把筛选过滤做到filter内部
     * @param data 筛选过的新数据集
     */
    fun update(data: MutableList<AppInfo>) {
        rawData = data
        filter.filter(currConstraint)
    }

    /**
     * 缓存的搜索内容，用于更新data后恢复搜索结果
     */
    private var currConstraint = ""

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                currConstraint = constraint.toString()
                val results = FilterResults()
                if (constraint.isEmpty()) {
                    // 全部内容
                    results.values = rawData
                    results.count = rawData.size
                } else {
                    // 处理输入的文本
                    val prefixString = constraint.toString().trim().lowercase(Locale.getDefault())
                    val filter = rawData.filter {
                        // 包名或label匹配，可以增加拼音匹配
                        (it.packageName.contains(prefixString)
                                || it.label.lowercase(Locale.getDefault()).contains(prefixString)
                                ||
                                (prefixString.contains("@")
                                        && it.sdkInfo.lowercase().contains(
                                    prefixString.replace("@", "")
                                )))
                    }
                    results.values = filter
                    results.count = filter.size
                }
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                // 更新搜索结果
                data = results.values as MutableList<AppInfo>
                notifyDataSetChanged()
            }

        }
    }

}