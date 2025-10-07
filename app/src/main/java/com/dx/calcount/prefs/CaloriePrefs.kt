package com.dx.calcount.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// SharedPrefs? - API that allows my app to store & retrieve
// my primitive data as key value pairs
class CaloriePrefs private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // getter and setter for maintenance calories
    var maintenanceCalories: Int
        get() = prefs.getInt(KEY_MAINTENANCE, DEFAULT_MAINTENANCE)
        set(value) {
            prefs.edit { putInt(KEY_MAINTENANCE, value) }
        }

    companion object {
        private const val PREFS_NAME = "calcount_prefs"
        private const val KEY_MAINTENANCE = "maintenance_calories"
        private const val DEFAULT_MAINTENANCE = 2000

        // volatile annotation ensures any write to this var is flushed to main mem
        // subsequent reads from other threads receive latest value, avoiding state mismatches
        @Volatile private var INSTANCE: CaloriePrefs? = null

        // singleton accessor - ensure only 1 instance exists and uses it thru the whole app
        fun getInstance(context: Context): CaloriePrefs =
            INSTANCE ?: synchronized(this) {
                val inst = CaloriePrefs(context)
                INSTANCE = inst
                inst
            }
    }
}


