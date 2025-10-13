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
import com.dx.calcount.ui.utils.ConfirmationDialog
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * wrapper model for RecyclerView supporting two distinct row types:
 *  - DayHeader: date label grouping meals by day
 *  - MealItem: actual meal card
 */
sealed class DayMealItem {
    data class DayHeader(val date: LocalDate) : DayMealItem()
    data class MealItem(val meal: Meal) : DayMealItem()
}

/**
 * RecyclerView adapter responsible for displaying a list of meals grouped by day headers.
 * handles both date separators and meal cards in a single list structure.
 * each meal card exposes click callbacks for open, add item, edit, and delete actions.
 */
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

    /**
     * replaces the adapter’s internal list with fresh dataset.
     * no diff util used — just brute force refresh for now. (change if got time)
     */
    fun submitList(list: List<DayMealItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * determines which layout 2 inflate per position.
     */
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DayMealItem.DayHeader -> TYPE_DAY_HEADER
            is DayMealItem.MealItem -> TYPE_MEAL
        }
    }

    /**
     * inflate either header row or a meal card depending on view type.
     */
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

    /**
     * delegate binding logic based on item type. (like a lightswitch heh)
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DayMealItem.DayHeader -> (holder as DayHeaderViewHolder).bind(item.date)
            is DayMealItem.MealItem -> (holder as MealViewHolder).bind(item.meal)
        }
    }

    override fun getItemCount() = items.size

    /**
     * ViewHolder for date header rows separating meal groups.
     * sisplays human-readable day labels (Today, Ystd, etc.).
     */
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

    /**
     * ViewHolder for individual meal cards.
     * handles text binding, color feedback, and all interaction callbacks.
     */
    inner class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val card: MaterialCardView = view.findViewById(R.id.meal_card)
        private val title: TextView = view.findViewById(R.id.meal_title)
        private val kcal: TextView = view.findViewById(R.id.meal_total_cals)
        private val time: TextView = view.findViewById(R.id.meal_time)
        private val btnAddItem: MaterialButton? = view.findViewById(R.id.btn_add_item)
        private val btnEdit: MaterialButton? = view.findViewById(R.id.btn_edit_meal)
        private val btnDelete: MaterialButton? = view.findViewById(R.id.btn_delete_meal)

        /**
         * binds meal data to view components and wires all click actions.
         * also applies color-coded feedback based on calorie total.
         */
        fun bind(meal: Meal) {
            title.text = meal.name
            kcal.text = "${meal.totalCalories} kcal"
            time.text = meal.date.format(DateTimeFormatter.ofPattern("HH:mm"))
            applyCaloriesTextColor(meal.totalCalories)
            itemView.setOnClickListener { onOpen(meal) }
            btnAddItem?.setOnClickListener { onAddItem(meal) }
            btnEdit?.setOnClickListener { onEdit(meal) }
            btnDelete?.setOnClickListener { 
                ConfirmationDialog.showDeleteMealConfirmation(itemView.context) {
                    onDelete(meal)
                }
            }
        }

        // partial AI assisted code
        /**
         * visually encodes calorie balance using hue shift:
         * - greenish for under maintenance
         * - reddish (or pinkish as ppl said) for over maintenance
         * intensity scales with deviation magnitude.
         */
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
