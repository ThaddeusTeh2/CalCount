package com.dx.calcount.data.model

// food item model
data class FoodItem(
    val id: Int,
    // f key
    val mealOwnerId: Int,
    val name: String,
    val calories: Int
)
