package com.dx.calcount.data.model.local

import androidx.room.*
import com.dx.calcount.data.model.local.entities.FoodItemEntity
import com.dx.calcount.data.model.local.entities.MealEntity
import com.dx.calcount.data.model.local.entities.MealsWithItems
import kotlinx.coroutines.flow.Flow

// here is where data calls is mapped to SQLITE queries
@Dao
interface MealDao {

    // classic CRUD for meal
    @Insert suspend fun insertMeal(meal: MealEntity): Long
    @Update suspend fun updateMeal(meal: MealEntity)
    @Delete suspend fun deleteMeal(meal: MealEntity)

    // and for meal items
    @Insert suspend fun insertItem(item: FoodItemEntity): Long
    @Update suspend fun updateItem(item: FoodItemEntity)
    @Delete suspend fun deleteItem(item: FoodItemEntity)

    // transaction annotation ensures all database operations -
    // either succeed & committed to db || if 1 operation fails
    // all changes are rolled back
    @Transaction
    // SQLITE query to get every meal between 2 specific times, sort them by timestamp, mapped to getMealsBetween
    @Query("SELECT * FROM meals WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getMealsBetween(start: Long, end: Long): Flow<List<MealsWithItems>>

    @Transaction
    // just a SQLITE query to get the meal by id, mapped to getMealWithItemsById
    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun getMealWithItemsById(mealId: Int): Flow<MealsWithItems?>


    // SQLITE query to find all food_items that belong to the mealId
    // sum the calories of those items
    // set the sum as int
    // return a value of 0(int) if meal has no assc items
    @Query("SELECT IFNULL(SUM(calories), 0) FROM food_items WHERE mealOwnerId = :mealId")
    suspend fun getTotalCaloriesForMealOnce(mealId: Int): Int

    // SQLITE query to update a meals total calories, mapped to updateMealTotal
    @Query("UPDATE meals SET totalCalories = :total WHERE id = :mealId")
    suspend fun updateMealTotal(mealId: Int, total: Int)
}