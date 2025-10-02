package com.dx.calcount.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dx.calcount.R
import com.dx.calcount.data.model.FoodItem

class FoodAdapter(private val onClick: (FoodItem) -> Unit) : RecyclerView.Adapter<FoodAdapter.VH>() {

    private val items = mutableListOf<FoodItem>()

    fun submitList(list: List<FoodItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_food_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.food_title)
        private val kcal: TextView = view.findViewById(R.id.food_total_cals)

        fun bind(fi: FoodItem) {
            name.text = fi.name
            kcal.text = "${fi.calories} kcal"
            itemView.setOnClickListener { onClick(fi) }
        }
    }
}