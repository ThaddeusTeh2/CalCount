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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class DayMealItem {
    data class DayHeader(val date: LocalDate) : DayMealItem()
    data class MealItem(val meal: Meal) : DayMealItem()
}

class DayMealAdapter(
    private val onOpen: (Meal) -> Unit,
    private val onAddItem: (Meal) -> Unit,
    private val onEdit: (Meal) -> Unit,
    private val onDelete: (Meal) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val items = mutableListOf<DayMealItem>()
    
    companion object {
        private const val TYPE_DAY_HEADER = 0
        private const val TYPE_MEAL = 1
    }

    fun submitList(list: List<DayMealItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DayMealItem.DayHeader -> TYPE_DAY_HEADER
            is DayMealItem.MealItem -> TYPE_MEAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DAY_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_header, parent, false)
                DayHeaderViewHolder(view)
            }
            TYPE_MEAL -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_card, parent, false)
                MealViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DayMealItem.DayHeader -> (holder as DayHeaderViewHolder).bind(item.date)
            is DayMealItem.MealItem -> (holder as MealViewHolder).bind(item.meal)
        }
    }

    override fun getItemCount() = items.size

    inner class DayHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dayText: TextView = view.findViewById(R.id.day_text)
        private val dateText: TextView = view.findViewById(R.id.date_text)

        fun bind(date: LocalDate) {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val tomorrow = today.plusDays(1)
            
            when {
                date == today -> {
                    dayText.text = "TODAY"
                    dateText.text = date.format(DateTimeFormatter.ofPattern("MMM dd"))
                }
                date == yesterday -> {
                    dayText.text = "YESTERDAY"
                    dateText.text = date.format(DateTimeFormatter.ofPattern("MMM dd"))
                }
                date == tomorrow -> {
                    dayText.text = "TOMORROW"
                    dateText.text = date.format(DateTimeFormatter.ofPattern("MMM dd"))
                }
                else -> {
                    dayText.text = date.format(DateTimeFormatter.ofPattern("EEE"))
                    dateText.text = date.format(DateTimeFormatter.ofPattern("MMM dd"))
                }
            }
        }
    }

    inner class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val card: MaterialCardView = view.findViewById(R.id.meal_card)
        private val title: TextView = view.findViewById(R.id.meal_title)
        private val kcal: TextView = view.findViewById(R.id.meal_total_cals)
        private val btnAddItem: MaterialButton? = view.findViewById(R.id.btn_add_item)
        private val btnEdit: MaterialButton? = view.findViewById(R.id.btn_edit_meal)
        private val btnDelete: MaterialButton? = view.findViewById(R.id.btn_delete_meal)

        fun bind(meal: Meal) {
            title.text = meal.name
            kcal.text = "${meal.totalCalories} kcal"
            applyCaloriesTextColor(meal.totalCalories)
            itemView.setOnClickListener { onOpen(meal) }
            btnAddItem?.setOnClickListener { onAddItem(meal) }
            btnEdit?.setOnClickListener { onEdit(meal) }
            btnDelete?.setOnClickListener { onDelete(meal) }
        }

        private fun applyCaloriesTextColor(total: Int) {
            val ctx = itemView.context
            val prefs = CaloriePrefs.getInstance(ctx)
            val maintenance = prefs.maintenanceCalories
            val diff = total - maintenance

            // Map deviation intensity (max 1000 kcal difference)
            val maxDiff = 1000f
            val intensity = kotlin.math.min(kotlin.math.abs(diff).toFloat(), maxDiff) / maxDiff

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
