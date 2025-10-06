package com.dx.calcount.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dx.calcount.R
import com.dx.calcount.data.model.Meal
import com.dx.calcount.prefs.CaloriePrefs
import com.google.android.material.button.MaterialButton
import kotlin.math.abs
import kotlin.math.min

class MealAdapter(
    private val onOpen: (Meal) -> Unit,
    private val onAddItem: (Meal) -> Unit,
    private val onEdit: (Meal) -> Unit,
    private val onDelete: (Meal) -> Unit
) : RecyclerView.Adapter<MealAdapter.VH>() {

    private val items = mutableListOf<Meal>()

//    fun submitList(list: List<Meal>) {
//        items.clear()
//        items.addAll(list)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
//        private val card: MaterialCardView = view.findViewById(R.id.meal_card)
        private val title: TextView = view.findViewById(R.id.meal_title)
        private val kcal: TextView = view.findViewById(R.id.meal_total_cals)
        private val btnAddItem: MaterialButton? = view.findViewById(R.id.btn_add_item)
        private val btnEdit: MaterialButton? = view.findViewById(R.id.btn_edit_meal)
        private val btnDelete: MaterialButton? = view.findViewById(R.id.btn_delete_meal)

        fun bind(m: Meal) {
            title.text = m.name
            kcal.text = "${m.totalCalories} kcal"
            applyCaloriesTextColor(m.totalCalories)

            itemView.setOnClickListener { onOpen(m) }
            btnAddItem?.setOnClickListener { onAddItem(m) }
            btnEdit?.setOnClickListener { onEdit(m) }
            btnDelete?.setOnClickListener { onDelete(m) }
        }

        // help from GPT to create color changing logic based on maintenance / actual variation
        private fun applyCaloriesTextColor(total: Int) {
            val ctx = itemView.context
            val prefs = CaloriePrefs.getInstance(ctx)
            val maintenance = prefs.maintenanceCalories
            val diff = total - maintenance

            // Map deviation intensity (max 1000 kcal difference)
            val maxDiff = 1000f
            val intensity = min(abs(diff).toFloat(), maxDiff) / maxDiff

            // Below/equal target = green, above = red
            val hue = if (diff <= 0) 120f else 0f
            val sat = 0.4f + 0.5f * intensity
            val value = 0.9f

            val hsv = floatArrayOf(hue, sat, value)
            val color = Color.HSVToColor(hsv)
            kcal.setTextColor(color)
        }
    }
}