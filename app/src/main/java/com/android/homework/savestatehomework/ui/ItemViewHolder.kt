package com.android.homework.savestatehomework.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.homework.savestatehomework.R
import com.android.homework.savestatehomework.db.model.Contact

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var tvName: TextView = view.findViewById(R.id.tv_name)
    private var tvPhone: TextView = view.findViewById(R.id.tv_phone)

    fun bind(data: Contact) {
        tvName.text = data.name
        tvPhone.text = data.phone
    }
}