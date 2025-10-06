package com.dx.calcount.prefs

import android.content.Context
import android.content.SharedPreferences

class CaloriePrefs private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var maintenanceCalories: Int
        get() = prefs.getInt(KEY_MAINTENANCE, DEFAULT_MAINTENANCE)
        set(value) {
            prefs.edit().putInt(KEY_MAINTENANCE, value).apply()
        }

    companion object {
        private const val PREFS_NAME = "calcount_prefs"
        private const val KEY_MAINTENANCE = "maintenance_calories"
        private const val DEFAULT_MAINTENANCE = 2000

        @Volatile private var INSTANCE: CaloriePrefs? = null

        fun getInstance(context: Context): CaloriePrefs =
            INSTANCE ?: synchronized(this) {
                val inst = CaloriePrefs(context)
                INSTANCE = inst
                inst
            }
    }
}


