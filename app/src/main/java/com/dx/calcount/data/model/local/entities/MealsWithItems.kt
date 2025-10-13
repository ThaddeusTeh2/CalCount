package com.dx.calcount.data.model.local.entities

import androidx.room.Embedded
import androidx.room.Relation

// define  database layer relationship
data class MealsWithItems(
    @Embedded val meal: MealEntity,
    @Relation(parentColumn = "id", entityColumn = "mealOwnerId")
    val items: List<FoodItemEntity>
)
