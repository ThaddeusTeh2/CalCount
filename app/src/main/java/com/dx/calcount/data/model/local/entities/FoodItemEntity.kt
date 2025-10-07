package com.dx.calcount.data.model.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// database layer entity for food items
@Entity(
    tableName = "food_items",
    // meal is the fkey
    foreignKeys = [ForeignKey(
        entity = MealEntity::class,
        parentColumns = ["id"],
        childColumns = ["mealOwnerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("mealOwnerId")]
)

data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealOwnerId: Int,
    val name: String,
    val calories: Int
)
