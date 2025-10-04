package com.dx.calcount.data.model.local

import androidx.room.*
import com.dx.calcount.data.model.local.entities.FoodItemEntity
import com.dx.calcount.data.model.local.entities.MealEntity
import com.dx.calcount.data.model.local.entities.MealsWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert suspend fun insertMeal(meal: MealEntity): Long
    @Update suspend fun updateMeal(meal: MealEntity)
    @Delete suspend fun deleteMeal(meal: MealEntity)

    @Insert suspend fun insertItem(item: FoodItemEntity): Long
    @Update suspend fun updateItem(item: FoodItemEntity)
    @Delete suspend fun deleteItem(item: FoodItemEntity)

    @Transaction
    @Query("SELECT * FROM meals WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getMealsBetween(start: Long, end: Long): Flow<List<MealsWithItems>>

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun getMealWithItemsById(mealId: Int): Flow<MealsWithItems?>

    @Query("SELECT IFNULL(SUM(calories), 0) FROM food_items WHERE mealOwnerId = :mealId")
    suspend fun getTotalCaloriesForMealOnce(mealId: Int): Int

    @Query("UPDATE meals SET totalCalories = :total WHERE id = :mealId")
    suspend fun updateMealTotal(mealId: Int, total: Int)
}