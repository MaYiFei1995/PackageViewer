package com.mai.packageviewer.adapter

import android.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mai.packageviewer.R
import com.mai.packageviewer.data.AppInfo
import com.squareup.picasso.Picasso


class AppAdapter(data: MutableList<AppInfo>) :
    BaseQuickAdapter<AppInfo, BaseViewHolder>(R.layout.item_app_info, data), Filterable {

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

    override fun getFilter(): Filter {
        return AppFilter(data)
    }

}