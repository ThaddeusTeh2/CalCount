package com.dx.calcount.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dx.calcount.MyApp
import com.dx.calcount.data.model.FoodItem
import com.dx.calcount.data.model.Meal
import com.dx.calcount.ui.adapter.DayMealItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

/**
 * responsible for managing meal and food item data.
 * provides data to the UI and handles background operations via coroutines.
 */
class MealViewModel(application: Application) : AndroidViewModel(application) {

    // access repository instance from the custom Application class
    private val repo = (application as MyApp).repo
    // system timezone for date conversions
    private val zone = ZoneId.systemDefault()

//    fun mealsFor(date: LocalDate): StateFlow<List<Meal>> {
//        val start = date.atStartOfDay().atZone(zone).toInstant().toEpochMilli()
//        val end = date.plusDays(1).atStartOfDay().atZone(zone).toInstant().toEpochMilli() - 1
//        return repo.getMealsBetween(start, end)
//            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
//    }


    /**
     * Fetch meals grouped by day across a defined date range (past and future).
     * @param daysBack days before today to include
     * @param daysForward after today to include
     * @return Flow emitting a list of DayMealItem (headers and meals)
     */
    fun mealsForMultipleDays(daysBack: Int = 365, daysForward: Int = 1): Flow<List<DayMealItem>> {
        val today = LocalDate.now()
        val startDate = today.minusDays(daysBack.toLong())
        val endDate = today.plusDays(daysForward.toLong())

        //  date range into timestamps for database queries
        val start = startDate.atStartOfDay().atZone(zone).toInstant().toEpochMilli()
        val end = endDate.plusDays(1).atStartOfDay().atZone(zone).toInstant().toEpochMilli() - 1

        // fetch meals between timestamps and transform into UI-friendly grouped data
        return repo.getMealsBetween(start, end)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
            .let { mealsFlow ->
                mealsFlow.map { meals ->
                    // group meals by local date for display
                    val groupedMeals = meals.groupBy { meal ->
                        meal.date.toLocalDate()
                    }
                    
                    val result = mutableListOf<DayMealItem>()
                    
                    // generate all dates in range
                    // iterate over each date in range and construct list of DayMealItems
                    var currentDate = startDate
                    while (!currentDate.isAfter(endDate)) {
                        val dayMeals = groupedMeals[currentDate] ?: emptyList()
                        if (dayMeals.isNotEmpty()) {
                            // add day header first
                            result.add(DayMealItem.DayHeader(currentDate))
                            // + each meal for that day
                            dayMeals.forEach { meal ->
                                result.add(DayMealItem.MealItem(meal))
                            }
                        }
                        currentDate = currentDate.plusDays(1)
                    }
                    
                    result
                }
            }
    }

    /**
     * fetch a single meal and its associated food items as a StateFlow.
     * used for meal detail views.
     * @param mealId unique meal identifier
     */
    fun mealWithItems(mealId: Int): StateFlow<Pair<Meal, List<FoodItem>>> {
        return repo.getMealWithItemsFlow(mealId)
            .stateIn(viewModelScope, SharingStarted.Eagerly,
                Pair(Meal(0, "", LocalDate.now().atStartOfDay(), 0), emptyList()))
    }

    /**
     * make a new meal in the database.
     * executes on the IO dispatcher for background work.
     * @param meal meal object to insert
     * @param onComplete callback returning the inserted meal ID on main thread
     */
    fun createMeal(meal: Meal, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.createMeal(meal)
            withContext(Dispatchers.Main) { onComplete(id) }
        }
    }
    /**
     * update an existing meal entry in the database.
     * executes in background via IO dispatcher.
     */
    fun updateMeal(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateMeal(meal)
        }
    }
    /**
     * Delete a meal and its related data from the database.
     * executes asynchronously on IO dispatcher.
     */
    fun deleteMeal(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteMeal(meal)
        }
    }
    /**
     * create a new food item linked to a meal.
     * runs insert in background and executes callback with item ID on main thread.
     */
    fun createItem(item: FoodItem, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.createItem(item)
            withContext(Dispatchers.Main) { onComplete(id) }
        }
    }
    /**
     * update an existing food item in the database.
     * uses IO dispatcher for background thread safety.
     */
    fun updateItem(item: FoodItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateItem(item)
        }
    }
    /**
     * delete a food item entry from the database.
     * runs asynchronously via coroutine on IO dispatcher.
     */
    fun deleteItem(item: FoodItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteItem(item)
        }
    }
}