package com.dx.calcount.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dx.calcount.R
import com.dx.calcount.data.model.FoodItem
import com.dx.calcount.ui.utils.ConfirmationDialog
import com.google.android.material.button.MaterialButton

/**
 * RecyclerView adapter for displaying a list of FoodItem objects.
 * handles edit and delete interactions for each food entry.
 * wrote serving as the base adapter for the food list inside a meal.
 */
class FoodAdapter(
    private val onEdit: (FoodItem) -> Unit,
    private val onDelete: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodAdapter.VH>() {

    // internal list holds food data
    private val items = mutableListOf<FoodItem>()

    /**
     * replaces the current list with a new one and refreshes the UI.
     */
    fun submitList(list: List<FoodItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * inflates layout for each food card and returns the view holder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_food_card, parent, false)
        return VH(v)
    }
    /**
     * binds a FoodItem to corresponding view holder.
     */
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    /**
     * returns total number of food items.
     */
    override fun getItemCount() = items.size

    /**
     * ViewHolder representing each food card inside the RecyclerView.
     * each card showed shld hold name, calories, and edit/delete buttons.
     */
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.food_title)
        private val kcal: TextView = view.findViewById(R.id.food_total_cals)
        private val btnEdit: MaterialButton? = view.findViewById(R.id.btn_edit_food)
        private val btnDelete: MaterialButton? = view.findViewById(R.id.btn_delete_food)

        /**
         * bind a single FoodItem to the UI elements.
         * also set up button listener for edit and delete.
         */
        fun bind(fi: FoodItem) {
            name.text = fi.name
            kcal.text = "${fi.calories} kcal"
            btnEdit?.setOnClickListener { onEdit(fi) }
            btnDelete?.setOnClickListener { 
                ConfirmationDialog.showDeleteFoodConfirmation(itemView.context) {
                    onDelete(fi)
                }
            }
        }
    }
}