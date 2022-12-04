package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val mainRepository = MainRepository(database)

    private val asteroidWeekList = mainRepository.asteroidsWeek
    private val asteroidTodayList = mainRepository.asteroidsToday
    private val asteroidSavedList = mainRepository.asteroidsSaved

    val asteroidList: MediatorLiveData<List<Asteroid>> = MediatorLiveData()

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetails: LiveData<Asteroid?>
        get() = _navigateToAsteroidDetails

    init {
        getAsteroids()
        getPictureOfDay()
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            mainRepository.refreshAsteroids()
            asteroidList.addSource(asteroidSavedList) {
                asteroidList.value = it
            }
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = mainRepository.getPictureOfDay()
            } catch (e: Exception) {
                Log.e("MainViewModel", e.message.toString())
            }
        }
    }

    fun onWeekAsteroidsClicked() {
        viewModelScope.launch {
            clearSource()
            asteroidList.addSource(asteroidWeekList) {
                asteroidList.value = it
            }
        }
    }

    fun onTodayAsteroidsClicked() {
        viewModelScope.launch {
            clearSource()
            asteroidList.addSource(asteroidTodayList) {
                asteroidList.value = it
            }
        }
    }

    fun onSavedAsteroidsClicked() {
        viewModelScope.launch {
            clearSource()
            asteroidList.addSource(asteroidSavedList) {
                asteroidList.value = it
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToAsteroidDetails.value = null
    }

    private fun clearSource() {
        asteroidList.removeSource(asteroidWeekList)
        asteroidList.removeSource(asteroidTodayList)
        asteroidList.removeSource(asteroidSavedList)
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}