package com.mai.packageviewer.adapter

import android.annotation.SuppressLint
import android.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mai.packageviewer.R
import com.mai.packageviewer.data.AppInfo
import com.squareup.picasso.Picasso
import java.util.*


class AppAdapter(data: MutableList<AppInfo>) :
    BaseQuickAdapter<AppInfo, BaseViewHolder>(R.layout.item_app_info, data), Filterable {

    private var rawData: MutableList<AppInfo> = data

    override fun convert(holder: BaseViewHolder, item: AppInfo) {

        holder.setText(R.id.appName, item.label)
        if (item.iconDrawable != null)
            holder.setImageDrawable(R.id.icon, item.iconDrawable)

        holder.setText(R.id.packageName, item.packageName)
        holder.setText(R.id.version, item.versionName)

        holder.getView<Button>(R.id.detail).setOnClickListener {
            Toast.makeText(context, "${item.versionCode}", Toast.LENGTH_SHORT).show()
        }
    }

    fun update(data:MutableList<AppInfo>){
        rawData = data
        filter.filter(currConstraint)
    }

    private var currConstraint = ""

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                currConstraint = constraint.toString()
                val results = FilterResults()
                if (constraint.isEmpty()) {
                    results.values = rawData
                    results.count = rawData.size
                } else {
                    val prefixString = constraint.toString().trim().toLowerCase(Locale.getDefault())
                    val filter = rawData.filter {
                        (it.packageName.contains(prefixString) || it.label.toLowerCase(Locale.getDefault()).contains(prefixString))
                    }
                    results.values = filter
                    results.count = filter.size
                }
                return results //过滤结果
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                data = results.values as MutableList<AppInfo>
                notifyDataSetChanged()
            }

        }
    }

}