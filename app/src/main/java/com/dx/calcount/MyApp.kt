package com.dx.calcount

import android.app.Application
import androidx.room.Room
import com.dx.calcount.data.MealRepository
import com.dx.calcount.data.model.local.AppDatabase

// initialize the database
class MyApp: Application() {
    lateinit var repo: MealRepository

    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "calcount.db"
        )
            .build()
        repo = MealRepository(db.mealDao())
    }
}