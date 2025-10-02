package com.dx.calcount.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dx.calcount.R
import com.dx.calcount.data.model.Meal

class MealAdapter (private val onClick: (Meal) -> Unit): RecyclerView.Adapter<MealAdapter.VH>() {
    private val items = mutableListOf<Meal>()

    fun submitList(list: List<Meal>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.meal_title)
        private val kcal: TextView = view.findViewById(R.id.meal_total_cals)

        fun bind(m: Meal) {
            title.text = m.name
            kcal.text = "${m.totalCalories} kcal"
            itemView.setOnClickListener { onClick(m) }
        }
    }
}