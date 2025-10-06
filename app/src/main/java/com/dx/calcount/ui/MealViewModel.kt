package com.dx.calcount.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dx.calcount.data.MealRepository
import com.dx.calcount.data.model.local.AppDatabase
import com.dx.calcount.data.model.FoodItem
import com.dx.calcount.data.model.Meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class MealViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = MealRepository(db.mealDao())
    private val zone = ZoneId.systemDefault()

    fun mealsFor(date: LocalDate): StateFlow<List<Meal>> {
        val start = date.atStartOfDay().atZone(zone).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay().atZone(zone).toInstant().toEpochMilli() - 1
        return repo.getMealsBetween(start, end)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    fun mealWithItems(mealId: Int): StateFlow<Pair<Meal, List<FoodItem>>> {
        return repo.getMealWithItemsFlow(mealId)
            .stateIn(viewModelScope, SharingStarted.Eagerly,
                Pair(Meal(0, "", LocalDate.now().atStartOfDay(), 0), emptyList()))
    }

    fun createMeal(meal: Meal, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.createMeal(meal)
            withContext(Dispatchers.Main) { onComplete(id) }
        }
    }

    fun updateMeal(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateMeal(meal)
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteMeal(meal)
        }
    }

    fun createItem(item: FoodItem, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.createItem(item)
            withContext(Dispatchers.Main) { onComplete(id) }
        }
    }

    fun updateItem(item: FoodItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateItem(item)
        }
    }

    fun deleteItem(item: FoodItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteItem(item)
        }
    }
}