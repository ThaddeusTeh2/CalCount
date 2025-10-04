package com.dx.calcount.data

import com.dx.calcount.data.model.local.MealDao
import com.dx.calcount.data.model.local.entities.FoodItemEntity
import com.dx.calcount.data.model.local.entities.MealEntity
import com.dx.calcount.data.model.local.entities.MealsWithItems
import com.dx.calcount.data.model.FoodItem
import com.dx.calcount.data.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MealRepository(private val dao: MealDao) {

    private val zone: ZoneId = ZoneId.systemDefault()

    private fun MealEntity.toDomain(): Meal =
        Meal(
            id = id,
            name = name,
            date = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zone),
            totalCalories = totalCalories
        )

    private fun FoodItemEntity.toDomain(): FoodItem =
        FoodItem(
            id = id,
            name = name,
            calories = calories,
            mealOwnerId = mealOwnerId
        )

    private fun MealsWithItems.toDomainMeal(): Meal =
        Meal(
            id = meal.id,
            name = meal.name,
            date = LocalDateTime.ofInstant(Instant.ofEpochMilli(meal.timestamp), zone),
            totalCalories = items.sumOf { it.calories }
        )

    private fun Meal.toEntity(): MealEntity =
        MealEntity(
            id = id,
            name = name,
            timestamp = date.atZone(zone).toInstant().toEpochMilli(),
            totalCalories = totalCalories
        )

    private fun FoodItem.toEntity(): FoodItemEntity =
        FoodItemEntity(
            id = id,
            mealOwnerId = mealOwnerId,
            name = name,
            calories = calories
        )

    // -- Flows exposed to UI
    fun getMealsBetween(startEpoch: Long, endEpoch: Long): Flow<List<Meal>> =
        dao.getMealsBetween(startEpoch, endEpoch).map { list -> list.map { it.toDomainMeal() } }

    fun getMealWithItemsFlow(mealId: Int): Flow<Pair<Meal, List<FoodItem>>> =
        dao.getMealWithItemsById(mealId).map { mwi ->
            if (mwi == null) {
                Pair(Meal(0, "", LocalDateTime.now(), 0), emptyList())
            } else {
                val mealDomain = mwi.toDomainMeal()
                val items = mwi.items.map { it.toDomain() }
                Pair(mealDomain, items)
            }
        }

    // -- CRUD (suspend) operations
    suspend fun createMeal(meal: Meal): Int {
        val newId = dao.insertMeal(meal.toEntity()).toInt()
        return newId
    }

    suspend fun updateMeal(meal: Meal) {
        dao.updateMeal(meal.toEntity())
    }

    suspend fun deleteMeal(meal: Meal) {
        dao.deleteMeal(meal.toEntity())
    }

    suspend fun createItem(item: FoodItem): Int {
        val newId = dao.insertItem(item.toEntity()).toInt()
        val total = dao.getTotalCaloriesForMealOnce(item.mealOwnerId)
        dao.updateMealTotal(item.mealOwnerId, total)
        return newId
    }

    suspend fun updateItem(item: FoodItem) {
        dao.updateItem(item.toEntity())
        val total = dao.getTotalCaloriesForMealOnce(item.mealOwnerId)
        dao.updateMealTotal(item.mealOwnerId, total)
    }

    suspend fun deleteItem(item: FoodItem) {
        dao.deleteItem(item.toEntity())
        val total = dao.getTotalCaloriesForMealOnce(item.mealOwnerId)
        dao.updateMealTotal(item.mealOwnerId, total)
    }
}