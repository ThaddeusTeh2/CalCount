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

    private val _maintenanceCalories = MutableLiveData<Int>(prefs.maintenanceCalories)
    val maintenanceCalories: LiveData<Int> = _maintenanceCalories

    fun setMaintenanceCalories(value: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.maintenanceCalories = value
            _maintenanceCalories.postValue(value)
        }
    }
}


