package com.dx.calcount.data.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dx.calcount.data.model.local.entities.FoodItemEntity
import com.dx.calcount.data.model.local.entities.MealEntity

// DO NOT CHANGE THE VERSION !!!!
@Database(entities = [MealEntity::class, FoodItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        // singleton accessor - ensure only 1 instance exists and uses it thru the whole app
        @Volatile private var INSTANCE: AppDatabase? = null

        // responsibility moved to MyApp
//        fun getInstance(context: Context): AppDatabase =
//            INSTANCE ?: synchronized(this) {
//                val inst = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "calcount.db"
//                ).build()
//                INSTANCE = inst
//                inst
//            }
    }
}