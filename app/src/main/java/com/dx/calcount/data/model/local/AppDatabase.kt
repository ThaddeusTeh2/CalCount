package com.dx.calcount.data.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dx.calcount.data.model.local.entities.FoodItemEntity
import com.dx.calcount.data.model.local.entities.MealEntity

@Database(entities = [MealEntity::class, FoodItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calcount.db"
                ).build()
                INSTANCE = inst
                inst
            }
    }
}