package com.android.homework.savestatehomework.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.homework.savestatehomework.R
import com.android.homework.savestatehomework.db.model.Contact

class ListAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    private val itemsList = mutableListOf<Contact>()

    fun updateItems(newItems: List<Contact>) {
        itemsList.clear()
        itemsList.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemsList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemsList.size
}