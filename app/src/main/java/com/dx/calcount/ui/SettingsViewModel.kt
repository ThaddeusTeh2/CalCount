package com.dx.calcount.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dx.calcount.prefs.CaloriePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = CaloriePrefs.getInstance(application)

    // MutableLiveData - internal viewmodel data holding & exposes public facing immutable data
    // as LiveData
    private val _maintenanceCalories = MutableLiveData<Int>(prefs.maintenanceCalories)

    // LiveData - lifecycle aware data holder class
    val maintenanceCalories: LiveData<Int> = _maintenanceCalories

    // update user-set maintenance calories value
    fun setMaintenanceCalories(value: Int) {
        // launches coroutine on IO dispatcher for non-blocking disk write 2 SharedPref
        viewModelScope.launch(Dispatchers.IO) {
            // persist new cals target 2 SharedPref via CalPrefs helper
            prefs.maintenanceCalories = value
            // post updated val 2 LiveData for UI observers on main thread
            _maintenanceCalories.postValue(value)
        }
    }
}


